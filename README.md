# Daily Foreign Exchange Rate

Implement a batch process that calls an API every day at **6:00 PM** to retrieve foreign exchange transaction data. The application will insert the **USD/NTD** exchange rate data and date (formatted as **yyyyMMdd**) into a table/collection.

---

## Key Features

### Scheduled Data Fetching
- **Daily Retrieval**: The application calls the **TAIFEX API** (GET) every day at **18:00** to retrieve the latest **USD/NTD** exchange rates.
- **Data Storage**: The data is inserted into a **MongoDB** collection, timestamped with the format **yyyy-MM-dd HH:mm:ss**.

### Historical Exchange Rate API
- **Date Range Retrieval**: Provides an API to retrieve historical **USD/NTD** exchange rate data based on a specified date range (up to **one year prior**).
- **Error Handling**: If the date range is invalid, the API responds with an **error code E001**.

### Unit Testing
- **Comprehensive Testing**: Thorough unit tests are implemented for both the batch processing feature and the Forex API to ensure the application functions correctly.

---