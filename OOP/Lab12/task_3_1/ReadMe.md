# task_3_1 — Кодування відео (Facade)

Рефакторинг із застосуванням шаблону **Фасад**.

Складну підсистему (CodecFactory, BitrateReader, AudioMixer, Codec, Buffer, VideoFile)
заховано за класом `VideoConversionFacade` з єдиним методом:

    public VideoFile convert(VideoFile file, Codec codec)

Усередині фасад: визначає вихідний кодек -> читає/декодує -> кодує в цільовий
формат -> відновлює аудіо. Клієнт (Main) тепер робить лише один виклик замість
багатокрокової ручної роботи з підсистемою. Класи підсистеми не змінювались.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
