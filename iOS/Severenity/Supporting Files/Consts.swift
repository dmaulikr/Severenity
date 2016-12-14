//
//  Consts.swift
//  Severenity
//
//  Created by Yura Yasinskyy on 29.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import Foundation

// MARK: Servers adresses

let kServerURL = "https://severenity.herokuapp.com"
let kPlacesURL = kServerURL + "/places/all"
let kUsersURL = kServerURL + "/users"
let kCreateUserURL = kServerURL + "/users/create"

// MARK: Alerts

let kNeedsLocationServicesAccess = "Unfortunately Severenity needs access to location services to continue. Please turn it manually in Settings and come back. App will now exit"
let kAuthError = "Sorry, you cannot be authorized now. Try again later."

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
let kSettingsInteractor = "SettingsInteractor"
let kInitialInteractor = "InitialInteractor"

// MARK: Log file name

let kLogFileName = "log.txt"

// MARK: Documents directory path

let kDocumentDirPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first

// MARK: Regex patterns

let kEmailRegexPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"
