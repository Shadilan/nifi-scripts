## Спавнение двух JSON файлов
 
 Первый JSON файл задаётся в GenerateFlowFile процессоре в разделе Properties Custom text.
 Значение атрибута "test1" будет случайным образом принимать значения от 1 до 2.
 ```
{
  "test2": "2",
  "test1": "${random():mod(2):plus(1)}"
}
```
Второй "эталонный" JSON файл задаётся в атрибуте script.etalon_json
```
{"test2": "2","test1": "1"}
```
 
 ![](https://github.com/vomikan/nifi-scripts/blob/main/JSON/Compare/nifi_sample.png?raw=true)
