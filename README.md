# Vinted Mail Updates Bot

The **Vinted Mail Updates Bot** notifies you via email whenever new items matching your search criteria are uploaded to Vinted. Since Vinted does not provide real-time notifications for specific search criteria, this bot periodically checks for updates and sends email notifications.

## Table of Contents
- [Overview](#overview)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
    - [Properties File Instructions](#properties-file-instructions)
    - [Where to Get the Vinted API URL](#where-to-get-the-vinted-api-url)
- [Running the Bot](#running-the-bot)
- [Customizing the Bot](#customizing-the-bot)
- [Automation Scripts](#automation-scripts)
    - [Startup Script](#startup-script)
    - [Stopping Script](#stopping-script)
    - [Setting Up a Cron Job](#setting-up-a-cron-job)
- [FAQ](#faq)

---

## Overview

The **Vinted Mail Updates Bot** is a lightweight Java application that periodically queries the Vinted API based on your specified criteria. When new items matching your search are found, it sends an email notification to a specified address.

---

## Getting Started

1. Clone the repository:
   ```bash
    git clone https://github.com/tlouro-c/vinted_bot.git
   ```

2. Navigate to the project directory:
    ```bash
    cd vinted_bot
   ```

3. Configure the config.properties file (see [Configuration](#configuration)).

4. Build the project or download the pre-built .jar file.

5. Run the bot using Java:
    ```bash
    java -jar path/to/vinted_mail_updates_bot.jar
   ```

## Configuration

The bot reads its configuration from the `resources/config.properties` file. Below are the required properties and their descriptions:

### Properties File Instructions

| Property          | Description                                                                                                                       | Example                                      |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------|
| `mail.smtp.host`  | The mail smpt host.                                                                                                               | `sender_example@gmail.com`                   |
| `sender.email`    | The email address used to send notifications. This should be a valid account.                                                     | `sender_example@gmail.com`                   |
| `sender.password` | The password for the sender email. Use an [App Password](https://support.google.com/accounts/answer/185833?hl=en) if using Gmail. | `example_password`                           |
| `receiver.email`  | The email address where notifications will be sent.                                                                               | `receiver_example@gmail.com`                 |
| `email.subject`   | The subject line for the notification emails.                                                                                     | `Vinted Notification - New Item`             |
| `interval`        | The frequency (in minutes) at which the bot checks for new items. Minimum recommended is `10` if you're using gmail.              | `10`                                         |
| `vinted.api.url`  | The API endpoint for Vinted search results. (See [Where to Get the Vinted API URL](#where-to-get-the-vinted-api-url))             | See example below                            |

---

### Example `config.properties` File
```properties
mail.smtp.host=smtp.gmail.com
sender.email=sender_example@gmail.com
sender.password=example_password
receiver.email=receiver_example@gmail.com
email.subject=Vinted Notification - New Item
interval=10
vinted.api.url=https://www.vinted.pt/api/v2/catalog/items?page=1&per_page=10&time=1732052982&search_text=&catalog_ids=1242&price_to=40&currency=EUR&order=newest_first&size_ids=&brand_ids=53&status_ids=&color_ids=&material_ids=
```

## Where to Get the Vinted API URL

Follow the steps below to retrieve the Vinted API URL and configure it for use in your project.

### Step 1: Perform a Search on Vinted
1. Visit the Vinted website: [https://www.vinted.com](https://www.vinted.com)
2. Apply your desired filters on the website. For example:
    - Select a category (e.g., Clothing, Accessories).
    - Choose a price range.
    - Filter by brand or other criteria as needed.

### Step 2: Inspect the Network Requests
1. Open your browser’s Developer Tools:
    - In Google Chrome, press `F12` or right-click anywhere on the page and select **Inspect**.
    - Switch to the **Network** tab in the Developer Tools.

### Step 3: Find the API Request
1. Refresh the page to ensure that the search filters are applied.
2. Look through the network requests and locate an API endpoint related to item listings. It will likely be something like `/api/v2/catalog/items`.
    - The request may be in the form of a GET request with parameters that include the filters you applied (e.g., category, price range).

### Step 4: Copy the API URL
1. Right-click on the request related to item listings and select **Copy** → **Copy as cURL** (or **Copy link address** depending on your browser).
2. This URL is the Vinted API URL you will use in your configuration.

### Step 5: Update `config.properties`
1. Open the `config.properties` file in your project.
2. Paste the copied URL into the `vinted.api.url` field. It should look something like this:

   ```properties
   vinted.api.url=https://www.vinted.com/api/v2/catalog/items?category_id=...&price_min=...&price_max=...
   ```

## Customizing the Bot

If you want to modify which products trigger an email notification, follow these steps:

1. **Edit the `App.java` file**:
    - Open the `App.java` file in your preferred IDE or text editor.
    - Navigate to the logic after **line 65** where the filtering for product criteria occurs.

2. **Modify the filtering logic**:
    - You can adjust the filtering criteria based on product price, brand, size, or other parameters.
- For example, you can update the price range or add conditions for specific brands:
  ```java
    // Example: Modify product filter logic in App.java
    Item relevantItem = null;
    for (int i = 0; relevantItem == null || !isRelevantSize(relevantItem); i++) {
        relevantItem = items.get(i);
    }
    
    if (lastItem == null || (!lastItem.equals(relevantItem))) {
        lastItem = relevantItem;
        EmailSender.sendEmail(EmailSender.formatItemAsHtml(lastItem));
    }
    ```   

---

## Running the Bot

1. **Run the bot manually**:
   To run the bot, navigate to the folder where the `.jar` file is located and execute the following command:
   ```bash
   java -jar path/to/vinted_mail_updates_bot.jar
   ```
   This will start the bot, which will periodically check for new items on Vinted and send email notifications.

2. **To prevent the bot from stopping when your terminal closes**, you can use a startup script that keeps the bot running in the background.

---

## Automation Scripts

### Startup Script

Create a script to ensure the bot runs continuously. Save this script as `start-vinted-mail-updates-bot.sh`:

```bash
#!/bin/bash

if pgrep -f "vinted_mail_updates_bot" > /dev/null; then
    echo "vinted_mail_updates_bot is already running."
else
    echo "Starting vinted_mail_updates_bot..."
    exec -a vinted_mail_updates_bot java -Dsun.java.command=vinted_mail_updates_bot -jar ~/path/to/your/jarfile/vinted_mail_updates_bot.jar &
fi
```

### Stopping Script

Create a script to stop the bot when needed. Save this as `stop-vinted-mail-updates-bot.sh`:

```bash
#!/bin/bash

PID=$(ps aux | grep 'vinted_mail_updates_bot' | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "Process not found!"
else
    echo "Killing process with PID: $PID"
    kill -9 $PID
fi
```

### Setting Up a Cron Job

To ensure the bot restarts if it stops, add the following entry to your crontab:

1. Open the crontab editor:
   ```bash
   crontab -e
   ```
2. Add this line to run the startup script every hour:
   ```bash
   0 */1 * * * /path/to/script/start-vinted-mail-updates-bot.sh
   ```

---

## FAQ

**What happens if the bot stops unexpectedly?**

The provided cron job will automatically restart the bot every hour if it’s not running.

**How do I customize the email notification format?**

You can modify the email template in the `App.java` file under the email sending logic.


