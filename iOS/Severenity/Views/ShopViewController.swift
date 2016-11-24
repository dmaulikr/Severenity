//
//  Tab1ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//
//  Well, well, does anybody read this?
//  This class is one small step for Severenity
//  And a giant leap for mankind. Niel Armstrong

import UIKit

class ShopViewController: UIViewController {
    
    internal var presenter: ShopPresenter?

    // MARK: Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = ShopPresenter()
        presenter?.delegate = self
        Log.info(message: "Shop VIPER module init did complete")
    }
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Log.info(message: "Shop Tab did load");
    }
    
    @IBAction func interactionTestButton(_ sender: AnyObject) {
        presenter?.shopViewEvent()
    }

}

// MARK: ShopPresenter delegate

extension ShopViewController: ShopPresenterDelegate {
    
    func shopPresenterDidCallView() {
        Log.info(message: "ShopViewController is called from ShopPresenter")
    }
    
}
