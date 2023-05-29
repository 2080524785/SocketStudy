package com.brave.www.rtmp;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

public class PushRTMP {
	static boolean exit = false;

	public static void run(){
		System.out.println("start...");
		//String rtmpPath = "rtmp://58.200.131.2:1935/livetv/gxtv";
		String rtsp = "rtsp://admin:a1234567@192.168.8.11:554/h264/ch0/main/av_stream";
		//String rtspPath = "rtmps://localhost:8443/oflaDemo/sssssss"; //oflaDemo
		//rtmp://localhost:1935/oflaDemo/sssssss
		String rtspPath = "rtmp://192.168.8.133:1935/live/test";

		int audioRecord = 1; // 0 = 不录制，1=录制
		boolean saveVideo = false;
		push(rtsp, rtspPath, audioRecord, saveVideo);
		System.out.println("end...");
	}

	@SuppressWarnings("resource")
	private static void push(String rtmpPath, String rtspPath, int audioRecord, boolean saveVideo){
		// 使用rtsp的时候需要使用 FFmpegFrameGrabber，不能再用 FrameGrabber
		int width = 640, height = 480;
		try {
			FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(rtmpPath);
			grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重

//			grabber.setImageWidth(width);
//			grabber.setImageHeight(height);
			System.out.println("grabber start");
			grabber.start();
			// 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
			FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(rtspPath, width, height, grabber.getAudioChannels());
			recorder.setInterleaved(true);
			recorder.setVideoOption("crf","25");
			recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 28
			recorder.setFormat("flv"); // rtmp的类型
			recorder.setFrameRate(25);
			recorder.setImageWidth(grabber.getImageWidth());
			recorder.setImageHeight(grabber.getImageHeight());
			recorder.setPixelFormat(0); // yuv420p

			recorder.setAudioChannels(grabber.getAudioChannels());
			recorder.setAudioBitrate(grabber.getAudioBitrate());
			recorder.setSampleRate(grabber.getSampleRate());

//			AVFormatContext fc = null;
//
//			if (rtspPath.indexOf("rtmp") >= 0 || rtspPath.indexOf("flv") > 0) {
//				// 封装格式flv
//				recorder.setFormat("flv");
//				recorder.setAudioCodecName("aac");
//				fc = grabber.getFormatContext();
//			}
			System.out.println("recorder start");
//			System.out.println(fc);
			recorder.start();

			System.out.println("all start!!");
			while (!exit) {
				Frame frame = grabber.grabImage();
				if (frame == null) {
					continue;
				}
				recorder.record(frame);
//				AVPacket avPacket = grabber.grabPacket();
//				if (avPacket != null && avPacket.size() > 0 && avPacket.data() != null) {
//					// 不需要编码直接把音视频帧推出去
//					recorder.recordPacket(avPacket);
//					avcodec.av_packet_unref(avPacket);
//				}

			}
			grabber.stop();
			grabber.release();
			recorder.stop();
			recorder.release();
		} catch (Exception e) {
			//不抛异常
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		run();
	}
}