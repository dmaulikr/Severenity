//
//  Consts.swift
//  Severenity
//
//  Created by Yura Yasinskyy on 29.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import Foundation

// MARK: Servers adresses

let kPlacesServerURL = "https://severenity.herokuapp.com/places/all"
let kSocketServerURL = "https://severenity.herokuapp.com"

// MARK: Alerts

let kNeedsLocationServicesAccess = "Unfortunately Severenity needs access to location services to continue. Please turn it manually in Settings and come back. App will now exit"

// MARK: VIPER Interactors

let kChatInteractor = "ChatInteractor"
let kMapInteractor = "MapInteractor"
let kNavigationBarInteractor = "NavigationBarInteracor"
let kProfileInteractor = "ProfileInteractor"
let kProfileListInteractor = "ProfileListInteractor"
let kProfileGridInteractor = "ProfileGridInteractor"
let kQuestsInteractor = "QuestsInteractor"
let kShopInteractor = "ShopInteractor"
let kTabBarInteractor = "TabBarInteractor"

// MARK: Log file name

let kLogFileName = "log.txt"

// MARK: Documents directory path

let kDocumentDirPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
