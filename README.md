Tenor Android Demo - Gif Download
=================================
This demo shows how to create a `Uri` using [Glide](https://github.com/bumptech/glide) and the GIF url provided by [Tenor](https://tenor.com/gifapi).


## Creating a `Uri` from a GIF Url
To create a `Uri` using the GIF `url` provided by [Tenor's API Response](https://tenor.com/gifapi#Result), the GIF content needs to be first stored in the file sytem of a phone.  The [GifDownloader][gifdownloader] class show in details on how this process is being done.  You can change the GIF storage location by modifying/overriding the `getGifDestination()`, `getGifStorageDir()` and `generateUniqueGifFileName()` method in the [GifDownloader][gifdownloader] class.


## MimeType
The file of GIF content created using [GifDownloader][gifdownloader] is set to be `image/gif`.  This is done by setting the suffix of the file name returned by `generateUniqueGifFileName()` with `.gif`.


## Committing GIF Content to an App
Google provides some excellent code snippets on how to commit GIF content to an app:
```java
/**
 * Commits a GIF image
 *
 * @param contentUri Content URI of the GIF image to be sent
 * @param imageDescription Description of the GIF image to be sent
 */
public static void commitGifImage(Uri contentUri, String imageDescription) {
    InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(
            contentUri,
            new ClipDescription(imageDescription, new String[]{"image/gif"}));
    InputConnection inputConnection = getCurrentInputConnection();
    EditorInfo editorInfo = getCurrentInputEditorInfo();
    Int flags = 0;
    if (android.os.Build.VERSION.SDK_INT >= 25) {
        flags |= InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
    }
    InputConnectionCompat.commitContent(
            inputConnection, editorInfo, inputContentInfo, flags, opts);
}
```
Google's full examples and explanations are available in [here](https://developer.android.com/guide/topics/text/image-keyboard.html#adding_image_support_to_imes).
