/**
 * Фасад (Facade) над складною підсистемою відеоконвертації.
 * Приховує роботу з CodecFactory, BitrateReader та AudioMixer,
 * надаючи клієнту єдиний простий метод convert(file, codec).
 */
public class VideoConversionFacade {

    /**
     * Перекодовує відеофайл у заданий формат.
     * @param file  відеофайл, який треба перекодувати
     * @param codec цільовий кодек (формат)
     * @return новий перекодований відеофайл
     */
    public VideoFile convert(VideoFile file, Codec codec) {
        System.out.println("VideoConversionFacade: conversion started.");

        // 1. Визначаємо кодек, яким стиснено вихідний файл
        Codec sourceCodec = CodecFactory.extract(file);

        // 2. Зчитуємо/декодуємо відеопотік
        Buffer buffer = BitrateReader.read(file, sourceCodec);

        // 3. Кодуємо у цільовий формат
        VideoFile result = BitrateReader.convert(buffer, file.getName(), codec);

        // 4. Відновлюємо аудіодоріжку
        new AudioMixer().fix(result, file.getAudioBuffer());

        System.out.println("VideoConversionFacade: conversion completed.");
        return result;
    }
}
