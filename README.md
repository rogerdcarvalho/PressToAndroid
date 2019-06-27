# PressToAndroid

## Introduction

PressToAndroid is a framework that allows you to easily package an existing WordPress site into an app. It can grab the content from any WordPress site that has enabled the [WordPress REST API](https://developer.wordpress.org/rest-api/). It then repackages up to 4 pages into native app tabs, and renders any blog posts into a dedicated 'Newsfeed' experience, similar to Facebook or Instagram. You don't need to understand any Java or Android concepts to use this framework, all options have been simplified into a single 'Configuration' class. If you do understand Java/Android you are free to fork and use, expand or change this framework for your own purposes under the [GNU General Public License] (https://www.gnu.org/licenses/gpl-3.0.en.html)

## Requirements

1. A working WordPress instance available on a publicly accessible domain name or IP address
2. A working WordPress REST API enabled on your WordPress instance. This tends to be enabled by default on a general vanilla WordPress installation. Certain hosting providers have disabled this when they try to sell specific WordPress plans or add-on services, but if you have a clean WordPress installation running on a normal general purpose webserver this should work.
3. A WordPress Theme that renders nicely on mobile devices. PressToAndroid will automatically remove headers, footers and sidebars to only render the general content, but it still helps to design your pages and posts in a way that they will look nice on mobile devices.
4. Your own app icon and a Google Play Developer account if you intend to distribute this app to users via the Google Play Store.
5. Android Studio to open and configure the project.

## Features

Once you've configured the template, you will have an app that is able to:

* Display up to 4 pages hosted by your WordPress. Whenever you change the content of the pages through the WordPress admin, they will automatically update in the app the next time the app is opened and connected to the internet.
* Display any blog posts you publish through a dedicated 'Newsfeed' experience, whenever the app is connected to the internet.
* Display any pages previously loaded offline when the device does not have access to the internet. 
* Any external HTML links within your Pages or Posts can either open using the default browser on the device (or dedicated apps for content links such as Facebook, YouTube, Instagram, etc.) or they can open within the App as in-app views.
* The basic template is a fully native Android binary, so you can add any Gradle plugins or native features you see fit. All WordPress content is loaded in Android fragments using a WebView.

## Instructions

1. Download the source code and open the root directory in Android Studio.
2. Wait for gradle to complete syncing
3. You can test whether the app works correctly by pressing the play button or 'Run' > 'Run 'app''. You should be able to run it on an Android device or simulator and see some sample PressToApp content.
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

## Additional customization

We've added some additional customization options for you. The following options are available:

* USE_LAZY_LOADING determines how to render your blog content. If set to true and you have a lot of articles, it will only load articles in batches, and as the user scrolls down additional content will be loaded. If set to false all blog content will be loaded whenever the app opens.
* LOAD_LINKS_EXTERNALLY determines how the app will treat hyperlinks in your content. If set to true, whenever the user clicks on a link, the app will open the default browser or app on the phone to open the content. If set to false, the app will load all content as if it is part of your app.
* REMOVE_WEBTITLE_CONTENT_AFTER_LAST_DASH keeps titles short. Any HTML titles displayed within the app will be limited to the last dash. Say the title of a certain page is 'About Us - MyWordpress Site Title', it will only display as 'About Us' in the app.
* Lastly, WEB_ELEMENTS_TO_REMOVE determines how the app should render your WordPress content. In order to make your WordPress more 'app-like' we remove elements such as headers, footers and sidebars so the content looks native to the app. If you see any content on your pages you want to remove, you can simply add their CSS classes here as `.put("<parent class name>","<child class name>");`. Alternatively if you want to keep certain content that the app removes by default, just delete the '.put' that mentions those classes.

## Advanced features

We are an app development agency that was hired by a client to create a native app from their WordPress. We decided to make the basic Android source code available under GNU so it would be easy for everyone to do the same. We can however support you if you want more advanced customizations. Some of the features we've built are:

* **In-app billing:** You can limit access to tabs or posts based on payment. You can use either one-time purchases or subscriptions to sell your users premium content. The app will ask the user to make a purchase through Google Play Billing before showing the content. We've developed a WordPress plugin that makes it easy for you to mark content as premium from your WordPress admin. Get in touch with us if you would like to use this functionality. 

* **Push Notifications:** You can send your users push notifications whenever new content is available. We've integrated this solution with Google Firebase and can even develop a push notification interface that you can control via WordPress. Get in touch if interested.

* **PressToiOS:** We've ported this framework to iOS. Because Apple is quite particular in their app review process and has specifically mentioned rejection criteria around 'repackaged websites' and 'template-based apps' we are a bit hesitant to make this open source. We've successfully released apps using our framework and want to prevent other people submitting 'bad' apps for review using this framework and then being flagged by Apple whenever their implementation doesn't meet App Review standards. This could lead to it becoming more difficult for us to get approved whenever we use the framework. If you would like to release your WordPress content on iOS please get in touch with us directly and we can see if we can support you.

You can get in touch directly via GitHub or https://presstoapp.com
