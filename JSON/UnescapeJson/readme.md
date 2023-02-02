(EN) Unescape all inner JSON strings in FlowFile
==============================================
Unescape JSON from all inner strings. 
The script is implemented for early versions of NiFi, where the unescapeJson function is missing.

In FlowFile Example:

`
[
  {
    "name1": "KIM Ltd.",
    "forma": "[{\"key0\":\"val0\",\"key1\":\"val1\",\"key2\":\"val2\"}]",
    "name2": ""
  }
]
`

Results of Script:

`
[
	{
		"name1": "Kim Ltd",
		"forma": [
			{
				"key0": "val0",
				"key1": "val1",
				"key2": "val2"
			}
		],
		"name2": ""
	}
]
`

(RU) Достает все вложенные JSON из экранированных строк. 
===================================================================
Скрипт реализован для ранних версий NiFi, где функция unescapeJson отсутствует.

*Пример входящего файла*

`
[
  {
    "name1": "KIM Ltd.",
    "forma": "[{\"key0\":\"val0\",\"key1\":\"val1\",\"key2\":\"val2\"}]",
    "name2": ""
  }
]
`


*Результаты для скрипта по данному файлу*

`
[
	{
		"name1": "Kim Ltd",
		"forma": [
			{
				"key0": "val0",
				"key1": "val1",
				"key2": "val2"
			}
		],
		"name2": ""
	}
]
`
