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
    
    override init() {
        super.init()
        (UIApplication.shared.delegate as! AppDelegate).viperInteractors["ProfileListInteractor"] = self
    }
    
    func profileListPresenterNeedsData() {
        print("ProfileList Interactor was called from ProfileList Presenter")
        getPlacesData()
    }
    
    func profileListPresenterAskForTransition(withParam param: Int) {
        let selector = #selector(MapInteractor.mapInteractorEvent(with:))
        let _ = (UIApplication.shared.delegate as! AppDelegate).viperInteractors["MapInteractor"]?.perform(selector, with: (self.placesData as! Array<Dictionary<String, AnyObject>>)[param])
    }
    
    /**- provideDataForList calls LocationsServerManager's instance
     and asks it to provide data that should be shown in UITableView.
     The result is returned in callback as Array with Dictionaries inside.
     Each dicitonary is a separate place later displayed in the table.*/
    private func getPlacesData() {
        locationsServerManager.provideData { (result) in
            self.placesData = result as [AnyObject]
            self.delegate?.profileListInteractorDidCallPresenter(withData: self.placesData as! Array<Dictionary<String, AnyObject>>)
        }
    }
}
