# wddOnboardingLibrary
![Screenshot_2020-06-09-12-18-25-23_28d2e796c586c3c535ec0c82b0c38245](https://user-images.githubusercontent.com/51435895/84117013-82643e00-aa4e-11ea-8b20-772366fdaf7f.jpg)  ![Screenshot_2020-06-09-12-18-29-97_28d2e796c586c3c535ec0c82b0c38245](https://user-images.githubusercontent.com/51435895/84117535-4bdaf300-aa4f-11ea-936f-62bf72bae6ef.jpg)  ![Screenshot_2020-06-09-12-18-34-68_28d2e796c586c3c535ec0c82b0c38245](https://user-images.githubusercontent.com/51435895/84118436-a032a280-aa50-11ea-832d-3f9aa2dfe9f2.jpg)    ![Screenshot_2020-06-09-12-18-45-40_28d2e796c586c3c535ec0c82b0c38245](https://user-images.githubusercontent.com/51435895/84118066-197dc580-aa50-11ea-9a25-481e10018330.jpg)


An Android Library written in Java, for Recognition of an Image 

[![Android API23+](https://img.shields.io/badge/Android-API_23+-green.svg)]()
  [![Java 6+](https://img.shields.io/badge/Java-6+-red.svg)]()
  [![License Apache 2.0](https://img.shields.io/badge/license-Apache%20License%202.0-red.svg)]()

## Installation

### gradle

Add it in your root build.gradle at the end of repositories:

```bash
allprojects {
  repositories {
	...
	maven { url 'https://jitpack.io' }
  }
}
```

Add dependency in your build.gradle.

```bash
	        implementation 'com.github.antinolabsdev:wddOnboardingLibrary:1.0'

```

## Permissions
Add permissions in your manifest.xml. Request permission from your app if you are using SDK Level 26+.

```bash
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

## Usage

	1.  Create the instance the of the CustomButton class
	2.  Connected with the parent layout.
	3.  Call the method of the custom layout and pass the parameters accessKey and secretKey
	4   Add the view with customLayout.

```java
       CustomButton relativeLayout = new CustomButton(MainActivity.this);
       CustomButton.KeyAccess("AccessKey","SecretKey");
       ConstraintLayout constraintLayout =  findViewById(R.id.parent);
       relativeLayout.init();
       constraintLayout.addView(relativeLayout);
 ```
 
 ## Contributing

- If you **need help** or you'd like to **ask a general question**
- If you **found a bug**, open a service request.
- If you **have a feature request**, open a service request.
- If you **want to contribute**, submit a pull request.
 
 ## License
[MIT](https://choosealicense.com/licenses/mit/)
