# wddOnboardingLibrary
![Screenshot_2020-06-09-12-18-25-23_28d2e796c586c3c535ec0c82b0c38245](https://user-images.githubusercontent.com/51435895/84117013-82643e00-aa4e-11ea-8b20-772366fdaf7f.jpg)  ![Screenshot_2020-06-09-12-18-29-97_28d2e796c586c3c535ec0c82b0c38245](https://user-images.githubusercontent.com/51435895/84117535-4bdaf300-aa4f-11ea-936f-62bf72bae6ef.jpg)  ![Screenshot_2020-06-09-12-18-34-68_28d2e796c586c3c535ec0c82b0c38245](https://user-images.githubusercontent.com/51435895/84118436-a032a280-aa50-11ea-832d-3f9aa2dfe9f2.jpg)    ![Screenshot_2020-06-09-12-18-45-40_28d2e796c586c3c535ec0c82b0c38245](https://user-images.githubusercontent.com/51435895/84118066-197dc580-aa50-11ea-9a25-481e10018330.jpg)
An Android Library written in Java, for Recognition of an Image 



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



```java
       CustomButton relativeLayout = new CustomButton(MainActivity.this);
       CustomButton.KeyAccess("AKIAZID35LK5ROAGCPHJ","wimdF3AVdcNLGsVzUcBv363/yjXjQ5Mr1fCX0nIm");
       final ConstraintLayout constraintLayout =  findViewById(R.id.parent);
       relativeLayout.init();
       constraintLayout.addView(relativeLayout);
 ```
 
 ## License
[MIT](https://choosealicense.com/licenses/mit/)
