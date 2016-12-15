//
//  ProfileListInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileListInteractor: NSObject {
    
    weak var delegate: ProfileListInteractorDelegate?
    private let locationsServerManager = PlacesService()
    private var placesData = [AnyObject]()
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors[kProfileListInteractor] = self
    }
    
    // MARK: ProfileListPresenter events
    
    func profileListPresenterNeedsData() {
        Log.info(message: "ProfileListInteractor was called from ProfileListPresenter", sender: self)
        getPlacesData()
    }
    
    func profileListPresenterAskForTransition(withParam param: Int) {
        let selector = #selector(MapInteractor.profileListViewEventWith(data:))
        if let places = (placesData as? Array<Dictionary<String, Any>>)?[param] {
            let _ = WireFrame.sharedInstance.viperInteractors[kMapInteractor]?.perform(selector, with: places)
        }
    }
    
    // MARK: Service interaction
    
    /**- provideDataForList calls LocationsServerManager's instance
     and asks it to provide data that should be shown in UITableView.
     The result is returned in callback as Array with Dictionaries inside.
     Each dicitonary is a separate place later displayed in the table.*/
    private func getPlacesData() {
        locationsServerManager.provideData { (result) in
            if let places = result as? Array<Dictionary<String, AnyObject>> {
                self.placesData = places as [AnyObject]
                self.delegate?.profileListInteractorDidCallPresenterWith(data: places)
            }
        }
    }
}
