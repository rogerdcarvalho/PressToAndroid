# PressToAndroid

## Introduction

PressToAndroid is a framework that allows you to easily package an existing WordPress site into an app. It can grab the content of any WordPress site that has enabled the [WordPress REST API](https://developer.wordpress.org/rest-api/). It then repackages up to 4 pages into native app tabs, and renders any blog posts into a dedicted 'Newsfeed' experience, similar to Facebook or Instagram. You don't need to understand any Java or Android concepts to use this framework, all options have been simplified into a single 'Configuration' class. If you do understand Java/Android you are free to fork and use, expand or change this framework for your own purposes under the [GNU General Public License] (https://www.gnu.org/licenses/gpl-3.0.en.html)

## Requirements

1. A working WordPress instance available on a publicly accessible domain name or IP address
2. A working WordPress REST API enabled on your WordPress instance. This tends to be enabled by default on a general vanilla WordPress installation. Certain hosting providers have disabled this when they try to sell specific WordPress plans or add-on services, but if you have a clean WordPress installation running on a normal general purpose webserver this should work.
3. A WordPress Theme that renders nicely on mobile devices. PressToAndroid will automatically remove headers, footers and sidebars to only render the general content, but it still helps to design your pages and posts in a way that they will look nice on mobile devices.
4. Your own app icon and a Google Play Developer account if you intend to distribute this app to users via the Google Play Store. 

## Features

Once you've configured the template, you will have an app that is able to:

* Display up to 4 pages hosted by your WordPress. Whenever you change the content of the pages, they will automatically update in the app the next time the app is opened and connected to the internet
* Display any blog posts you publish through a dedicated 'Newsfeed' experience, whenever the app is connected to the internet
* Display any pages previously loaded offline when the device does not have access to the internet. 
* Any external HTML links within your Pages or Posts can either open using the default browser on the device (or dedicated apps for content links such as Facebook, YouTube, Instagram, etc.) or they can open within the App as in-app views.
* The basic template is a fully native Android binary, so you can add any Gradle plugins or native features you see fit. All WordPress content is loaded in an Android fragment using a WebView.

## Instructions

1. Download the source code and open the root directory in Android Studio.
2. Wait for gradle to complete syncing
3. You can test whether the app works correctly by pressing the play button or 'Run' > 'Run 'app''. You should be able to run it on an android device or simulator and see some sample PressToApp content.
4. Now you're ready to adapt the framework to your website. Open AndroidManifest.xml under 'app/manifests'
5. You'll see `package="net.rdcmedia.presstoandroid"` on line 4. This is the package name. It needs to be changed to whatever domain your website is, to ensure everyone that submits this package to Google Play has a unique identifier. 
   * Let's assume your domain is mywordpress.com. 
   * Hover your mouse over the word 'net', right click the mouse and then select 'Refactor' > 'Rename'.
   * Click 'Rename Package'
   * Type 'com'
   * Click 'Refactor'
   * On the bottom of the screen click 'Do Refactor'
   * Now hover your mouse over the word 'rdcmedia', right click the mouse and then select 'Refactor' > 'Rename'.
   * Click 'Rename Package'
   * Type 'mywordpress'
   * Click 'Refactor' 
   * On the bottom of the screen click 'Do Refactor'
5. The package name has now been changed to your website.
6. Now open 'app/java/com.mywordpress.presstoandroid/Configuration'
7. You should see `public static String WORDPRESS_URL = "https://presstoapp.com";`. Change 'https://presstoapp.com' to your website domain or ip
8. The line below mentions `public static String[] TAB_ICONS = {"ic_action_news", "ic_action_list_2", "ic_action_help", "ic_action_dialog"};`. Those are the icons to be used for whatever tabs you want to create in your app. You can change these to whatever icons you feel are suitable. We've added a bunch of free-to-use icons under 'app/res/mipmap'. But you can also load additional icons into this folder to use for your tabs. If you want to use less than 4 tabs, simply delete the 4th, 3rd, etc. variable.
9. The line below mentions `public static final boolean SHOW_BLOG = true;`. If you don't have any blog content on your WordPress or you don't want to app to render this, change it to false.
10. The line below, `public static String POSTS_TAB = "Showcase";` determines the name of the blog tab. You can change it to whatever you like. If you have put the line above to false, it will be ignored.
11. Lastly, the line `public static String[] PAGE_TABS = {"features", "questions", "contact"};` is where you select which WordPress content should be loaded in your tabs. PressToAndroid uses the 'slug' names you've configured in WordPress as the tab names. So make sure the references you add here actually exists as page slugs on your WordPress site. You can add upto 4, and they will use the icons you added before under 'TAB_ICONS'.
12. You are done! You can now run the app again as per step 3 and you should see your very own content rendered as a native app. Any content changes you want to make can just be handled from your WordPress admin. 
