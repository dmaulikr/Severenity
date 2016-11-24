//
//  HealthKitService.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 02.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import HealthKit

class HealthKitService: NSObject {

    static let sharedInstance = HealthKitService()
    var healthStore: HKHealthStore?
    
    // MARK: Init
    
    private override init() {
        super.init()
        
        if HKHealthStore.isHealthDataAvailable() {
            healthStore = HKHealthStore()
        }
        else {
            Log.info(message: "HealthKit is not available")
        }
        Log.info(message: "HealthKitService shared instance init did complete")
    }
}
