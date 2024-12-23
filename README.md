## Grails Simple SFTP Plugin (https://grails.org/plugin/simple-sftp) 
Simple way to connect and perform basic file actions to an SFTP server. This version is adopted to Grails 7

## Description
Allows application to upload, download, delete, rename, create directory to an SFTP Server with one method call. The connections and boilerplate codes are all handled by the plugin. The plugin uses JCraft JSch (http://www.jcraft.com/jsch/) for its SSH2 implementation. The client can be authenticated via private key or password.

## Dependency & Installation
Add plugin to ```grails-app/conf/BuildConfig.groovy```
```groovy
dependencies {
    implementation 'com.jcraft:jsch:0.1.55'
    implementation 'org.grails.plugins:simple-sftp:0.4-SNAPSHOT'
}
```

## Config
Add config to ```grails-app/conf/application.yml```
```groovy
simpleSftp: 
   server='qwerty.houston.com'
   username='helloworld'
   password='' // Leave empty string if you are using a private key, if password has a value it will overwrite the private key.
   remoteDir='/path_to_remote_dir/my_dir'
   port=22
   keyFilePath='/path_to_pk/my_pk.ppk'
   throwException=false // set to true if you want to handle the exceptions manually.
```

## Usage
Available methods
```groovy
def instrm = IOUtils.toInputStream("upload file text 1", StandardCharsets.UTF_8)
simpleSftpService.uploadFile(instrm, "textfile1.txt")
simpleSftpService.removeFile("textfile1.txt")
simpleSftpService.renameFile("textfile2.txt", "textfile_renamed.txt")
```

## Sample code
Inject the service class, from there you can call the uploadFile(), downloadFile(), etc.
```groovy
class MyController {
	// inject the service class.
    SimpleSftpService simpleSftpService

    def myMethod() {
        simpleSftpService.uploadFile(instrm, "textfile1.txt")
        simpleSftpService.removeFile("textfile1.txt")
        simpleSftpService.renameFile("textfile2.txt", "textfile_renamed.txt")
        simpleSftpService.downloadFile("textfile1.txt")
        simpleSftpService.createDir("temp")
    }
}
```
