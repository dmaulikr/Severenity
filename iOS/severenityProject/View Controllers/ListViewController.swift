//
//  ListViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ListViewController: UITableViewController {
    
    // MARK: - Managing table data
    
    fileprivate var dataForList = [AnyObject]()
    
    weak var activityIndicatorView: UIActivityIndicatorView!
    let locationsServerManager = LocationsServerManager()
    
    /**- provideDataForList calls LocationsServerManager's instance
     and asks it to provide data that should be shown in UITableView.
     The result is returned in callback as Array with Dictionaries inside.
     Each dicitonary is a separate place later displayed in the table.*/
    func provideDataForList() {
        locationsServerManager.provideData { (result) in
            self.dataForList = result as [AnyObject]
            self.tableView.reloadData()
        }
    }
    
    // MARK: - Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib(nibName: "ListCell", bundle: nil), forCellReuseIdentifier: "CellInList")
        
        // Data loading indicator for the UITableView
        let activityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: UIActivityIndicatorViewStyle.gray)
        activityIndicatorView.color = UIColor.blue
        tableView.backgroundView = activityIndicatorView
        self.activityIndicatorView = activityIndicatorView
        activityIndicatorView.startAnimating()

        provideDataForList()
    }
    
    // MARK: - Table view delegate methods
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        // probably need to refactor this pyramid of doom with guard statement and get tabs by name, not indexes
        if let mapViewController = self.tabBarController?.viewControllers?[2].childViewControllers.first {
            if let mapVC = mapViewController as? MapViewController {
                if let dataToPass = dataForList[(indexPath as NSIndexPath).row] as? [String : AnyObject] {
                    mapVC.recievedLocation = dataToPass
                    print(mapVC.recievedLocation)
                    self.tabBarController?.selectedIndex = 2;
                }
            }
        }
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    override func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        
        if (indexPath as NSIndexPath).row == (tableView.indexPathsForVisibleRows?.last as NSIndexPath?)?.row {
            activityIndicatorView.stopAnimating()
        }
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataForList.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: UITableViewCell
        if let c = tableView.dequeueReusableCell(withIdentifier: "CellInList", for: indexPath) as? ListCell {
            c.listCellTitle.text = dataForList[(indexPath as NSIndexPath).row]["name"] as? String
            cell = c
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: "CellInList", for: indexPath)
        }
        return cell
    }
}
