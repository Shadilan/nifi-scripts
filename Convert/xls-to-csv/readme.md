# Преобразуем XLS в CSV

Для преобразования используем библиотеку [Apache POI - the Java API for Microsoft Documents](https://poi.apache.org/) версии 5.0.0
[![latest packaged version(s)](https://poi.apache.org/images/project-header.png )](https://archive.apache.org/dist/poi/release/bin/poi-bin-5.0.0-20210120.zip)

Все jar файлы сохранены в одну папку  /opt/nifi/lib/poi

Простейший Excel файл создан для демонстранции

![image](https://user-images.githubusercontent.com/6836805/177600089-01809627-4ac6-408a-a545-1e2e2eb41da2.png)

В итоге работы Groovy скрипта получается CSV файл. 
```
"Col1","Col2","Col3",""
"123.0","bbbbb","0.17",""
```

Скрипт работает с файлами office 97+ в формате .xls.

Настройки процессора ExecuteGroovyScript 1.13.2
```
/opt/nifi/lib/poi/poi-5.0.0.jar; /opt/nifi/lib/poi/commons-math3-3.6.1.jar
```

![image](https://user-images.githubusercontent.com/6836805/177602470-8231e833-6a24-4e1d-a4e2-7f7772ebffe6.png)
