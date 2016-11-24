//
//  GridViewControllerCollectionViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileGridViewController: UICollectionViewController {
    
    internal var presenter: ProfileGridPresenter?
    internal var dataForList = [String]()
    
    // MARK: Init
    
    override init(collectionViewLayout layout: UICollectionViewLayout) {
        super.init(collectionViewLayout: layout)
        presenter = ProfileGridPresenter()
        presenter?.delegate = self
        Log.info(message: "ProfileGrid VIPER module init did complete")
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
    
        collectionView?.register(UINib(nibName: "ProfileGridCell", bundle: nil), forCellWithReuseIdentifier: "ProfileCellInGrid")
        collectionView?.backgroundColor = UIColor.black
        
        presenter?.provideProfileGridData()
    }

    // MARK: UICollectionView data source

    override func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }


    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 75
    }

    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ProfileCellInGrid", for: indexPath)
        return cell
    }
}

// MARK: ProfileGridPresenter delegate

extension ProfileGridViewController: ProfileGridPresenterDelegate {
    
    func profileGridPresenterDidCallView(withData data: [String]) {
        Log.info(message: "ProfileGridPresenter did call ProfileGridViewController")
    }
    
}
