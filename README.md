# Kleinanzeigen Ads Renewer

This project aims to automate those annoying emails from Kleinanzeigen telling you that your ad is about to expire in
about a week.

This program connects to your email account via IMAP and fetches the last 100 emails and then checks if they are from
Kleinanzeigen.
It then simply calls the URL in the email to renew the ad and move the email to another folder in your email account
depending on if they success or not.

## Installation

The easiest way to set up the application is to use Docker and docker-compose.
Just save the file below as ``docker-compose.yml`` and run ``docker-compose up -d`` to run the application in the
background.
It's recommended to run it on a server so that it can periodically check for new mail.
I can recommend using Portainer to manage your Docker containers, you just need to paste the file below into a stack and
run it. That's it.

But before you start the application, you need to set up the credentials to access your mail account.
It's possible to use several mail accounts.

### Gmail

For Gmail, you need to create an application password to use the application if you have two-factor authentication
enabled.
Simply visit [this page](https://myaccount.google.com/apppasswords) to create the application password.

The hostname for Gmail is ``imap.gmail.com``

### iCloud

With iCloud, you also need to create an application password [here](https://account.apple.com/account/manage) to sign
in.

The host name for iCloud is: ``imap.mail.me.com``

```yaml
services:
  app:
    image: ghcr.io/tutorialwork/kleinanzeigen-ads-renewer:1.0.1
    environment:
      - IMAP_SERVERS_GMAIL_HOST=imap.gmail.com
      - IMAP_SERVERS_GMAIL_USERNAME=YOUR_GMAIL_EMAIL
      - IMAP_SERVERS_GMAIL_PASSWORD=YOUR_GMAIL_APP_PASSWORD
      - IMAP_SERVERS_ICLOUD_HOST=imap.mail.me.com
      - IMAP_SERVERS_ICLOUD_USERNAME=YOUR_ICLOUD_EMAIL
      - IMAP_SERVERS_ICLOUD_PASSWORD=YOUR_ICLOUD_APP_PASSWORD
      - IMAP_FOLDERS_PROCESSED_FOLDER_NAME=Kleinanzeigen Mails # Optional, name of the folder where the mails get moved after successfully renewing the ad
      - IMAP_FOLDERS_FAILED_FOLDER_NAME=Kleinanzeigen Failed Mails # Optional, name of the folder where the mails get moved after the renewing failed 3 times
```