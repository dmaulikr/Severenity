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
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.

        //let keychain = Keychain()
        //let kGoogleAPIKey = keychain["kGoogleAPIKey"] ?? ""
        
        // In next two calls we can easily use kGoogleAPIKey but just for some test purposes I use my own key for now
        // Feel free to use kGoogleAPIKey
        Log.info(message: "Google Maps API key provided: \(GMSServices.provideAPIKey("AIzaSyB0vX6YGdJlcx9IB7LNSUakHbYRdA_DmBw"))", sender: self)
        Log.info(message: "Google Places API key provided: \(GMSPlacesClient.provideAPIKey("AIzaSyB0vX6YGdJlcx9IB7LNSUakHbYRdA_DmBw"))", sender: self)
        SocketService.sharedInstance.establishConnection()
        getHealthKitData()
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
        if let file = kDocumentDirPath?.appendingPathComponent(kLogFileName) {
            if FileManager.default.fileExists(atPath: file.path) {
                do  {
                    let fileHandle = try FileHandle(forWritingTo: file)
                    fileHandle.closeFile()
                } catch {
                    Log.error(message: "Closing log file failed: \(error)", sender: self)
                }
            }
        }
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
        FBSDKAppEvents.activateApp()
        let fbToken = FBSDKAccessToken.current()
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if (fbToken != nil) {
            window?.rootViewController = storyboard.instantiateViewController(withIdentifier: "loggedInController")
            UserService.sharedInstance.authorizeUserWith(userId: (fbToken?.userID)!) { success in
                Log.info(message: "User session restored", sender: self)
            }
        } else {
            window?.rootViewController = storyboard.instantiateViewController(withIdentifier: "loginController")
        }
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    // MARK: Get HealthKit data
    // TODO: This method is here just for test purposes now. It should be moved to the corresponding Interactor/Service
    private func getHealthKitData() {
        let today = Date()
        let calendar = Calendar.current
        let startDate = calendar.date(byAdding: .day, value: -90, to: today)
        
        var stepsCount = 0.0
        HealthService.sharedInstance.retrieveStepsCount(startDate: startDate!, endDate: today) { result in
            stepsCount = result
            
            // IMPORTANT: For testing purposes it's okey to have this log here but in any other case it should be removed because it's PHI data
            Log.info(message: "Total steps retrieved from HealthKit: \(stepsCount)", sender: self)
        }
        
        var totalDistance = 0.0
        HealthService.sharedInstance.retrieveWalkRunDistance(startDate: startDate!, endDate: today) { result in
            totalDistance = result
            
            // IMPORTANT: For testing purposes it's okey to have this log here but in any other case it should be removed because it's PHI data
            Log.info(message: "Total distance walked/run retrieved from HealthKit: \(totalDistance) miles", sender: self)
        }
        
    }
}
