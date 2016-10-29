//
//  MapInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import CoreLocation

class MapInteractor: NSObject {

    weak var delegate: MapInteractorDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["MapInteractor"] = self
    }
    
    // MARK: - Other events
    
    func profileListViewEvent(with data: AnyObject) {
        print("MapInteractor event happened from ProfileListView with data: \(data)")
        if let recievedData = data as? Dictionary<String,Any> {
            delegate?.displayPlace(with: recievedData as Dictionary<String, Any>)
        }
    }
    
    // MARK: - Service interaction
    
    func processUserLocationUpdate(with dictionary: Dictionary<String,Any>) {
        print("MapInteractor was called from MapPresenter to process new user location")
        SocketService.sharedInstance.sendLocationToServer(with: dictionary)
    }
    
    func processNewPlayerLocation(with dictionary: Dictionary<String,Any>) {
        if let lat = dictionary["lat"] as? Double,
            let lng = dictionary["lng"] as? Double,
            let fbUserId = dictionary["id"] as? String {
                FacebookService.sharedInstance.getFBProfilePicture(with: fbUserId) { (image) in
                    FacebookService.sharedInstance.getFBProfileInfo(with: fbUserId, and: { (info) in
                        self.delegate?.displayPlayer(with: image, and: CLLocationCoordinate2DMake(lat, lng), and: info)
                    })
            }
        }
    }
}
