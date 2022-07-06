# Преобразуем XLS в CSV

Для преобразования используем библиотеку [Apache POI - the Java API for Microsoft Documents](https://poi.apache.org/) версии 5.0.0
[![latest packaged version(s)](https://poi.apache.org/images/project-header.png )](https://archive.apache.org/dist/poi/release/bin/poi-bin-5.0.0-20210120.zip)

Простейший Excel файл создан для демонстранции

![image](https://user-images.githubusercontent.com/6836805/177600089-01809627-4ac6-408a-a545-1e2e2eb41da2.png)

В итоге работы Groovy скрипта получается CSV файл. 
```
"Col1","Col2","Col3",""
"123.0","bbbbb","0.17",""
```

Скрипт адаптирован для работы со старыми файлами Excel 95.
