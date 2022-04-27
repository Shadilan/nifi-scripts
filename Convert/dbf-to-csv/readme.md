# Преобразуем DBF файл в CSV﻿

Демо файл предваритеьно преобразован в base64, чтобы его можно было разместить в GenerateFlowFile процессор в виде текста.
Base64EncodeContent процессор преобразует его обратно в бинарный файл.
Третьим шагом, groovy скрипт преобразует DBF файл в CSV. 
 
 ![](https://github.com/vomikan/nifi-scripts/blob/main/Convert/dbf-to-csv/nifi_sample.png?raw=true)
 
 Используем библиотеку из проекта https://github.com/albfernandez/javadbf
 
 В свойствах ExecuteGroovyScript указываем путь к файлу
 
 | Property | Value |
|----------|-------|
|    Additional classpath      |   /opt/nifi/lib/dbf/javadbf-1.13.2.jar    |
