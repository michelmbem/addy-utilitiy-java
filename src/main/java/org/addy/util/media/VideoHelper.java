package org.addy.util.media;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class VideoHelper {
    private VideoHelper() {}

    public static BufferedImage extractFrame(File videoFile, float relativePosition) {
        BufferedImage capturedFrame = null;
        int frameNumber;

        try {
            SeekableByteChannel channel = NIOUtils.readableFileChannel(videoFile.getPath());

            try (MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(channel)) {
                DemuxerTrack videoTrack = demuxer.getVideoTrack();

                if (relativePosition >= 1)
                    frameNumber = (int) relativePosition;
                else if (relativePosition >= 0)
                    frameNumber = (int) (videoTrack.getMeta().getTotalFrames() * relativePosition);
                else
                    frameNumber = (int) (videoTrack.getMeta().getTotalFrames() + relativePosition);
            }

            Picture picture = FrameGrab.getFrameFromFile(videoFile, frameNumber);
            capturedFrame = AWTUtil.toBufferedImage(picture);
        } catch (IOException | JCodecException e) {
            e.printStackTrace();
        }

        return capturedFrame;
    }

    public static BufferedImage extractFrame(String fileName, float relativePosition) {
        return extractFrame(new File(fileName), relativePosition);
    }
}
