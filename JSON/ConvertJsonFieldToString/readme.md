# Cкрипт преобразует вложенное поле в строку

В текущем примере требуется преобразовать вложенную запись к JSON строке. Для больших вложенностей, массивов метод EscapeJson процессора UpdateRecord не отрабатывает.

На входе:

```json
[ {
    "metadata" : {
      "integrationTimestamp" : 1681505098676
    },
    "messageObject" : {
      "id" : "key",
      "createdOn" : "2023-03-29T16:32:44.8800000Z",
      "modifiedOn" : "2023-03-29T16:32:44.8800000Z",
      "currentAmount" : 1149.00,
      "contact" : "key",
      "type" : "main",
      "dueDate" : "2024-04-13T21:00:00.0000000Z",
      "regDate" : "2023-03-31T00:00:00.0000000Z",
      "totalAmount" : 1149.00,
      "bonusBalanceBPMID" : "key",
      "bonusBalanceStateName" : "Updated",
      "bonusBalanceState" : { },
      "bonuses" : [ {
        "typeSource" : "Sale",
        "currentAmount" : 149.00,
        "totalAmount" : 149.00,
        "dueAmount" : 149.00,
        "dueDate" : "2024-04-13T21:00:00.0000000Z"
      }, {
        "typeSource" : "Action",
        "currentAmount" : 1000.00,
        "totalAmount" : 1000.00,
        "dueAmount" : 1000.00,
        "dueDate" : "2023-05-05T16:19:54.1393545Z"
      } ],
      "contactBPMID" : "key"
    }
  } ]
  ```

На выходе:

```json
[ {
    "metadata" : {
      "integrationTimestamp" : 1681505099863
    },
    "messageObject" : {
      "id" : "key",
      "createdOn" : "2022-01-05T00:02:26.4100000Z",
      "modifiedOn" : "2022-01-05T00:02:26.4100000Z",
      "currentAmount" : 7149.0,
      "contact" : "key",
      "type" : "main",
      "dueDate" : "2024-03-17T21:00:00.0000000Z",
      "regDate" : "2022-11-26T00:00:00.0000000Z",
      "totalAmount" : 7149.0,
      "bonusBalanceBPMID" : "key",
      "bonusBalanceStateName" : "Updated",
      "bonuses" : "[{\"typeSource\":\"Sale\",\"currentAmount\":1153.00,\"totalAmount\":1153.00,\"dueAmount\":1153.00,\"dueDate\":\"2024-03-17T21:00:00.0000000Z\"},{\"typeSource\":\"Action\",\"currentAmount\":5996.00,\"totalAmount\":5996.00,\"dueAmount\":3000.00,\"dueDate\":\"2023-05-01T21:01:33.0478528Z\"}]",
      "contactBPMID" : "key"
    }
  } ]
  ```
