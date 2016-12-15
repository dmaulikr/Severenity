//
//  MapInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import CoreLocation
import Alamofire

class MapInteractor: NSObject {

    weak var delegate: MapInteractorDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors[kMapInteractor] = self
    }
    
    // MARK: Other events
    
    func profileListViewEventWith(data: AnyObject) {
        Log.info(message: "MapInteractor event happened from ProfileListView with data: \(data)", sender: self)
        if let recievedData = data as? Dictionary<String,Any> {
            delegate?.displayPlaceWith(dictionary: recievedData)
        }
    }
    
    // MARK: Interaction with services
    
    func processUserLocationUpdate(with dictionary: Dictionary<String,Any>) {
        Log.info(message: "MapInteractor was called from MapPresenter to process new user location", sender: self)
        SocketService.sharedInstance.sendLocationToServer(with: dictionary)
    }
    
    func processNewPlayerLocationWith(dictionary: Dictionary<String,Any>) {
        if let lat = dictionary["lat"] as? Double,
            let lng = dictionary["lng"] as? Double,
            let fbUserID = dictionary["id"] as? String {
            FacebookService.sharedInstance.getFBProfilePicture(for: fbUserID, size: .normal) { image in
                    FacebookService.sharedInstance.getFBProfileInfo(with: fbUserID, and: { info in
                        self.delegate?.displayPlayerWith(picture: image, coordinates: CLLocationCoordinate2DMake(lat, lng), info: info)
                    })
            }
        }
    }
    
    func getPlacesNearCoordinates(lng: Double, lat: Double) {
        let requestURL = "/places?lng=\(lng)&lat=\(lat)&radius=1000" // search radius in meters
        Alamofire.request(kServerURL + requestURL).responseJSON { response in
            guard let result = response.result.value as? Dictionary<String,Any>, result["result"] as? String == "success",
                let places = result["data"] as? Array<Dictionary<String,Any>> else {
                Log.error(message: "Response does not contain any places.", sender: self)
                return
            }
            Log.info(message: "Places neard coordinates loaded", sender: self)
            for place in places {
                guard let location = place["location"] as? NSDictionary, let lat = (location["coordinates"] as? NSArray)?[1] as? Double,
                let lng = (location["coordinates"] as? NSArray)?[0] as? Double else {
                    Log.error(message: "Cannot find 'location' attribute in response.", sender: self)
                    return
                }
                let placeParsed: Dictionary<String,Any> = ["placeId":place["placeId"] ?? "",
                                                           "name":place["name"] ?? "",
                                                           "type":place["type"] ?? "",
                                                           "createdDate":place["createdDate"] ?? "",
                                                           "lat":lat,
                                                           "lng":lng]
                self.delegate?.displayPlaceWith(dictionary: placeParsed)
            }
        }
    }
    
}
