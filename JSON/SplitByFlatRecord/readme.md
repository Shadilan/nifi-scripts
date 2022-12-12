(EN) Split JSON FlowFile to flat JSON FlowFile
==============================================
Get JSON with inner record in it and split it to different flowfiles each of that flowfile contains flate JSON.

In FlowFile Example:

`
{"id": "1", "type": "cost", "value": 1000, "lines": [{"id":1,"description": "field1","value": 100,"types": [{"name":"item"},{"name": "valuable"}]},{"id":2,"description": "field2","value": 900,"types": [{"name":"item"},{"name": "valuable"}]}],  "persons": [{"name": "Johnson","type": "seller"},{"name": "Simpson","type": "buyer"}]}
`

Results of Script:

`
{"id":"1","type":"cost","value":1000}
`

`
{"parent_id":"1","main_id":"1","name":"Johnson","type":"seller"}
{"parent_id":"1","main_id":"1","name":"Simpson","type":"buyer"}
`

`
{"parent_id":"1","main_id":"1","id":1,"description":"field1","value":100}
{"parent_id":"1","main_id":"1","id":2,"description":"field2","value":900}
`

`
{"parent_id":"1","main_id":"1","name":"item"}
{"parent_id":"1","main_id":"1","name":"valuable"}
{"parent_id":"1","main_id":"1","name":"item"}
{"parent_id":"1","main_id":"1","name":"valuable"}
`


works only with LineByLine JSON File

(RU) Разделяет JSON файл с иерархической структурой на плоские JSON
===================================================================
Разбивает входящий JSON на отдельные файлы в каждом из которых набор строк JSON с плоской структурой. ID достает из поля \_id или id и прокидывает в дочерние файлы.

*Пример входящего файла*

`
{"id": "1", "type": "cost", "value": 1000, "lines": [{"id":1,"description": "field1","value": 100,"types": [{"name":"item"},{"name": "valuable"}]},{"id":2,"description": "field2","value": 900,"types": [{"name":"item"},{"name": "valuable"}]}],  "persons": [{"name": "Johnson","type": "seller"},{"name": "Simpson","type": "buyer"}]}
`


*Результаты для скрипта по данному файлу*

`
{"id":"1","type":"cost","value":1000}
`

`
{"parent_id":"1","main_id":"1","name":"Johnson","type":"seller"}
{"parent_id":"1","main_id":"1","name":"Simpson","type":"buyer"}
`

`
{"parent_id":"1","main_id":"1","id":1,"description":"field1","value":100}
{"parent_id":"1","main_id":"1","id":2,"description":"field2","value":900}
`

`
{"parent_id":"1","main_id":"1","name":"item"}
{"parent_id":"1","main_id":"1","name":"valuable"}
{"parent_id":"1","main_id":"1","name":"item"}
{"parent_id":"1","main_id":"1","name":"valuable"}
`

Работает только для формат JSON Line by Line
