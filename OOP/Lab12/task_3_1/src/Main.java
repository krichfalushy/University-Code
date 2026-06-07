public class Main {

    public static void main(String[] args) {
        VideoFile videoFile = new VideoFile("youtubevideo.ogg",
                new Buffer("Byte buffer of video"),
                new Buffer("Byte buffer of audio"));

        // Спроба програти не тим кодеком
        videoFile.play(new MPEG4CompressionCodec());

        System.out.println();

        // Уся складність захована за фасадом — клієнту достатньо одного виклику
        VideoConversionFacade converter = new VideoConversionFacade();
        VideoFile mp4 = converter.convert(videoFile, new MPEG4CompressionCodec());

        System.out.println();
        mp4.play(new MPEG4CompressionCodec());   // тепер програється правильно
    }
}
