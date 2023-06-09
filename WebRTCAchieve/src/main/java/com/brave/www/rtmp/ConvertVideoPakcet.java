package com.brave.www.rtmp;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.FrameRecorder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Java opencv 实现拉取网络摄像头rtsp转rtmp（转封装方式）传输到流媒体服务器  
 *  @author JW  
**/

@Slf4j
public class ConvertVideoPakcet {


	private static final Map<String,ConvertVideoPakcet> convertVideoPakcets = new HashMap<>();

	private FFmpegFrameGrabber grabber = null;
	private FFmpegFrameRecorder record = null;
	private int width = -1, height = -1;

	// 视频参数
	private int audiocodecid;
	private int codecid;
	private double framerate;// 帧率
	private int bitrate;// 比特率

	// 音频参数
	private int audioChannels;
	private int audioBitrate;
	private int sampleRate;

	//控制程序循环
	private Boolean flag = true;


	private static ConvertVideoPakcet get(String deviceId){
		return convertVideoPakcets.get(deviceId);
	}

	public static Boolean start(String deviceId,String formUrl,String toUrl){

		if(null != get(deviceId)) return true;

		final ConvertVideoPakcet convertVideoPakcet = new ConvertVideoPakcet();
		convertVideoPakcets.put(deviceId,convertVideoPakcet);

		new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("start device");
				try {
					convertVideoPakcet.from(formUrl).to(toUrl).start();
				} catch (IOException e) {
					log.error("start dvice error,{}",e);
				}
			}
		}).start();

		log.info("start device finish!");
		return true;
	}

	/**
	 * 停止当前直播
	 * @param id
	 * @return
	 */
	public static Boolean stop(String id){
		log.info("stop device ,{}",id);
		ConvertVideoPakcet convertVideoPakcet = get(id);

		if(null != convertVideoPakcet){
			convertVideoPakcets.remove(id);
			return convertVideoPakcet.stop();
		}

		return false;
	}

	/**
	 * 拉取摄像头视频源
	 *
	 * @param src rtsp数据源地址
	 * @author JW
	 * @throws Exception
	 */
    private ConvertVideoPakcet from(String src) throws Exception {
		// 采集/抓取器
		grabber = new FFmpegFrameGrabber(src);
		if (src.indexOf("rtsp") >= 0) {
			grabber.setOption("rtsp_transport", "tcp");
		}
		grabber.start();// 开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
		if (width < 0 || height < 0) {
			width = grabber.getImageWidth();
			height = grabber.getImageHeight();
		}
		// 视频参数
		audiocodecid = grabber.getAudioCodec();
		log.warn("音频编码：{}",audiocodecid);
		codecid = grabber.getVideoCodec();
		framerate = grabber.getVideoFrameRate();// 帧率
		bitrate = grabber.getVideoBitrate();// 比特率
		// 音频参数
		// 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
		audioChannels = grabber.getAudioChannels();
		audioBitrate = grabber.getAudioBitrate();
		if (audioBitrate < 1) {
			audioBitrate = 128 * 1000;// 默认音频比特率
		}
		return this;
	}

	/**
	 * rtmp输出推流到nginx媒体流服务器
	 *
	 * @param out t\ rtmp媒体流服务器地址
	 * @author JW
	 * @throws IOException
	 */
    private ConvertVideoPakcet to(String out) throws IOException {
		// 录制/推流器
		record = new FFmpegFrameRecorder(out, width, height);
		record.setVideoOption("crf", "30");
		record.setGopSize(2);
		record.setFrameRate(framerate);
		record.setVideoBitrate(bitrate);

		record.setAudioChannels(audioChannels);
		record.setAudioBitrate(audioBitrate);
		record.setSampleRate(sampleRate);

		AVFormatContext fc = null;

		if (out.indexOf("rtmp") >= 0 || out.indexOf("flv") > 0) {
			// 封装格式flv
			record.setFormat("flv");
			record.setAudioCodecName("aac");
			record.setVideoCodec(codecid);
			fc = grabber.getFormatContext();
		}

		record.start(fc);

		return this;
	}

	/**
	 * 转封装
	 *
	 * @author eguid
	 * @throws IOException
	 */
	private void start() throws IOException {

	    //刷新开始的测试数据
		if(null != grabber)
			grabber.flush();

		while (flag) {
			AVPacket pkt = null;
			try {
				// 没有解码的音视频帧
				pkt = grabber.grabPacket();
				if (pkt == null || pkt.size() <= 0 || pkt.data() == null) {
					continue;
				}

				// 不需要编码直接把音视频帧推出去
				record.recordPacket(pkt);
				avcodec.av_packet_unref(pkt);

				try {
					Thread.sleep(100,1000);
				} catch (InterruptedException e) {
					log.error("推流发生等待异常,{}",e);
				}

			} catch (Exception e) {
				log.error("推流发生异常,{}",e);
			} catch (IOException e) {
				log.error("推流发生IO异常,{}",e);
			}
		}
	}

	private Boolean stop() {

		//控制退出循环
		flag = false;

		if(null != record){
			try {
				record.release();
			} catch (FrameRecorder.Exception e) {
				log.error("stop record error ,{}",e);
				return false;
			}
		}

		if(null != grabber){
			try {
				grabber.release();
			} catch (Exception e) {
				log.error("stop grabber error ,{}",e);
				return false;
			}
		}


		return true;
	}

//	public static void main(String[] args) throws Exception, IOException {
//
//		// 运行，设置视频源和推流地址rtsp://admin:a1234567@192.168.8.11:554/h264/ch0/main/av_stream
//		//rtmp://58.200.131.2:1935/livetv/cctv15
//		//rtsp://admin:admin12345@192.168.8.12:554/cam/realmonitor?channel=1&subtype=0
//		new ConvertVideoPakcet().from("rtsp://admin:a1234567@192.168.8.11:554/h264/ch0/main/av_stream")
//				.to("f://test.flv").start();
//	}
}