# Ten Test Cases

| ID | Test | Input / Action | Expected Result |
|---|---|---|---|
| TC01 | Launch application | Run the app | Stock Overview opens without login |
| TC02 | Search valid product | Enter `iPhone` | Only matching product rows are displayed |
| TC03 | Search unknown product | Enter `Unknown` | `No matching products found` is displayed |
| TC04 | Add stock | Add 5 units with employee name | Stock increases by 5 and an audit record is created |
| TC05 | Remove stock | Remove 2 units with employee name | Stock decreases by 2 and an audit record is created |
| TC06 | Empty quantity validation | Leave quantity empty | Validation message appears and no record is saved |
| TC07 | Zero quantity validation | Enter 0 | Validation message appears and no record is saved |
| TC08 | Empty employee validation | Leave employee name empty | Validation message appears and no record is saved |
| TC09 | Excessive removal | Remove more than current stock | Update is rejected and stock cannot become negative |
| TC10 | Monthly graph filter | Choose a product and month | Chart and opening/highest/lowest/closing values update |
