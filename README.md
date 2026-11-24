# Blutrixx Share File

A [NativePHP Mobile](https://nativephp.com) 4 plugin for Android's share list. NativePHP's `Share` facade still makes
the bridge calls `Share.File` and `Share.Url`, but v4's core no longer answers them; this plugin does.

Composer package: `blutrixx/nativephp-share-file` · Repo: `joelnjoshkibona/nativephp-share-file`

## Installation

```bash
composer require blutrixx/nativephp-share-file
```

Then list `Blutrixx\ShareFile\ShareFileServiceProvider` in your app's `NativeServiceProvider::plugins()`.

## Use

From PHP, NativePHP's own facade (`Native\Mobile\Facades\Share`), or from the web view with the bridge
(`POST /_native/api/call`):

| Function | Params | Answer |
|---|---|---|
| `Share.File` | `filePath`, `title`, `message` | `{ shared: true }` or `{ error: "failed" }` |
| `Share.Url` | `url`, `title`, `text` | `{ shared: true }` or `{ error: "failed" }` |

The file is copied into the app's cache folder (the one the app's FileProvider serves) and shared from there as a
read-only link, so the other app never gets the app's own files. The MIME type comes from the file's extension.
