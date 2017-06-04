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
    private var healthStore: HKHealthStore?
    
    // MARK: Init
    
    private override init() {
        super.init()
        if HKHealthStore.isHealthDataAvailable() {
            healthStore = HKHealthStore()
            
            var readTypes = Set<HKObjectType>()
            readTypes.insert(HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.stepCount)!)
            readTypes.insert(HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.distanceWalkingRunning)!)
            healthStore?.requestAuthorization(toShare: nil, read: readTypes) { (success, error) -> Void in
                if success {
                    Log.info(message: "HealthKit authorized succesfully", sender: self)
                } else {
                    Log.error(message: "HealthKit authorization failed: \(String(describing: error))", sender: self)
                }
            }
        }
        else {
            Log.info(message: "HealthKit is not available", sender: self)
        }
        Log.info(message: "HealthKitService shared instance init did complete", sender: self)
    }
    
    // MARK: Methods for getting data from HealthKit
    
    func retrieveStepsCount(startDate: Date, endDate: Date,
                           completion: @escaping (_ stepsRetrieved: Double) -> Void) {
        //   Define the Step Quantity Type
        let stepsCount = HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.stepCount)
        
        //  Set the Predicates & Interval
        let predicate = HKQuery.predicateForSamples(withStart: startDate, end: endDate, options: .strictStartDate)
        var interval = DateComponents()
        interval.day = 1
        
        //  Perform the Query
        let query = HKStatisticsCollectionQuery(quantityType: stepsCount!, quantitySamplePredicate: predicate,
                                                options: [.cumulativeSum], anchorDate: startDate, intervalComponents: interval)
        
        query.initialResultsHandler = { query, results, error in
            if error != nil {
                Log.error(message: "Cannot retrieve HealthKit steps data: \(String(describing: error))", sender: self)
                return
            }
            if let myResults = results {
                var stepsCount = 0.0
                myResults.enumerateStatistics(from: startDate, to: endDate) {
                    statistics, stop in
                    if let quantity = statistics.sumQuantity() {
                        let steps = quantity.doubleValue(for: HKUnit.count())
                        stepsCount += steps
                    }
                }
                completion(stepsCount)
            }
        }
        healthStore?.execute(query)
    }
    
    func retrieveWalkRunDistance(startDate: Date, endDate: Date,
                            completion: @escaping (_ distanceRetrieved: Double) -> Void) {
        //   Define the Distance Quantity Type
        let distance = HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.distanceWalkingRunning)
        
        //  Set the Predicates & Interval
        let predicate = HKQuery.predicateForSamples(withStart: startDate, end: endDate, options: .strictStartDate)
        var interval = DateComponents()
        interval.day = 1
        
        //  Perform the Query
        let query = HKStatisticsCollectionQuery(quantityType: distance!, quantitySamplePredicate: predicate,
                                                options: [.cumulativeSum], anchorDate: startDate, intervalComponents: interval)
        
        query.initialResultsHandler = { query, results, error in
            if error != nil {
                Log.error(message: "Cannot retrieve HealthKit distance data: \(String(describing: error))", sender: self)
                return
            }
            if let myResults = results {
                var totalDistance = 0.0
                myResults.enumerateStatistics(from: startDate, to: endDate) {
                    statistics, stop in
                    if let quantity = statistics.sumQuantity() {
                        let distance = quantity.doubleValue(for: HKUnit.mile())
                        totalDistance += distance
                    }
                }
                completion(totalDistance)
            }
        }
        healthStore?.execute(query)
    }

    
}
