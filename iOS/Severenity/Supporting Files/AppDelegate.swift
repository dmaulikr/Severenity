//
//  AppDelegate.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import FBSDKCoreKit
import FBSDKLoginKit
import GoogleMaps
import GooglePlaces

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.

        let keychain = Keychain()
        let kGoogleAPIKey = keychain["kGoogleAPIKey"] ?? ""
        
        // In next two calls we can easily use kGoogleAPIKey but just for some testing purposes I use my own key for now
        // Feel free to use kGoogleAPIKey
        print("Google Maps API key provided: \(GMSServices.provideAPIKey("AIzaSyB0vX6YGdJlcx9IB7LNSUakHbYRdA_DmBw"))")
        print("Google Places API key provided: \(GMSPlacesClient.provideAPIKey("AIzaSyB0vX6YGdJlcx9IB7LNSUakHbYRdA_DmBw"))")
        
        SocketService.sharedInstance.establishConnection()

        return FBSDKApplicationDelegate.sharedInstance().application(application, didFinishLaunchingWithOptions: launchOptions)
    }
    
    func application(_ application: UIApplication,
                     open url: URL,
                             sourceApplication: String?,
                             annotation: Any) -> Bool {
        return FBSDKApplicationDelegate.sharedInstance().application(
            application,
            open: url,
            sourceApplication: sourceApplication,
            annotation: annotation)
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        SocketService.sharedInstance.closeConnection()
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        
        let fbToken = FBSDKAccessToken.current()
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if (fbToken != nil) {
            let vc = storyboard.instantiateViewController(withIdentifier: "loggedInController")
            window?.rootViewController = vc
        } else {
            let vc = storyboard.instantiateViewController(withIdentifier: "loginController")
            window?.rootViewController = vc
        }

        FBSDKAppEvents.activateApp()
    }

    func applicationWillTerminate(_ application: UIApplication) {
        let loginManager: FBSDKLoginManager = FBSDKLoginManager()
        loginManager.logOut()
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
}
