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
    
    fileprivate var dataForList = []
    
    weak var activityIndicatorView: UIActivityIndicatorView!
    
    func provideDataForList() {
        
        let locationsServerManager = LocationsServerManager()
        locationsServerManager.provideData { (result) in
            self.dataForList = result as! [Any]
            self.tableView.reloadData()
        }
    }
    
    // MARK: - Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib(nibName: "ListCell", bundle: nil), forCellReuseIdentifier: "CellInList")
        
        // Data loading indicator for the tableview
        let activityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: UIActivityIndicatorViewStyle.gray)
        activityIndicatorView.color = UIColor.blue
        tableView.backgroundView = activityIndicatorView
        self.activityIndicatorView = activityIndicatorView
        activityIndicatorView.startAnimating()

        provideDataForList()
        
    }
    
    // MARK: - Table view delegate methods
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        // probably need to refactor this pyramid of doom with guard statement
        if let mapViewController = self.tabBarController?.viewControllers?[2].childViewControllers.first {
            if let mapVC = mapViewController as? MapViewController {
                if let dataToPass = dataForList[(indexPath as NSIndexPath).row] as? [String : AnyObject] {
                    mapVC.recievedLocation = dataToPass
                    self.tabBarController?.selectedIndex = 2;
                }
            }
        } else {
            tableView.deselectRow(at: indexPath, animated: true)
        }

//        if let mapVC = (self.tabBarController?.viewControllers?[2].childViewControllers.first)! as? MapViewController {
//            mapVC.recievedLocation = dataForList[indexPath.row] as! [String : AnyObject]
//            self.tabBarController?.selectedIndex = 2;
//        }

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
        
        if let cell = tableView.dequeueReusableCell(withIdentifier: "CellInList", for: indexPath) as? ListCell {
            
            cell.listCellTitle.text = dataForList[(indexPath as NSIndexPath).row]["name"] as? String
            return cell
            
        }
        else {
            
            let cell = tableView.dequeueReusableCell(withIdentifier: "CellInList", for: indexPath)
            return cell
        }
    }

}
