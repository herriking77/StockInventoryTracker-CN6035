# Runtime Test Results - 31 August 2026

| ID | Test | Observed result | Status |
|---|---|---|---|
| TC01 | Launch application | Stock Overview opened without login and showed eight products | PASS |
| TC02 | Search existing product | Search `iphone` returned iPhone 16 Pro, ELEC-001, 25 units | PASS |
| TC03 | Search unknown product | Search `Huawei` displayed `No matching products found.` | PASS |
| TC04 | Add stock | AirPods Pro increased from 43 to 48 after adding 5 units; employee and reason were recorded | PASS |
| TC05 | Remove stock | AirPods Pro decreased from 48 to 45 after removing 3 units; employee and reason were recorded | PASS |
| TC06 | Empty employee validation | `Employee name cannot be empty.` was displayed and update was blocked | PASS |
| TC07 | Empty quantity validation | `Quantity cannot be empty.` was displayed and update was blocked | PASS |
| TC08 | Excessive removal validation | Removing 100 from stock 45 was rejected with `Removal cannot exceed the current stock.` | PASS |
| TC09 | Adjustment history | History displayed the +5 and -3 AirPods Pro transactions with timestamp, employee and reason | PASS |
| TC10 | Monthly stock graph | August 2026 graph displayed opening 46, highest 46, lowest 43 and closing 45 | PASS |
