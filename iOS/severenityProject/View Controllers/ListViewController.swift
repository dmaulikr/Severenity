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
    
    private var dataForList = []
    
    func provideDataForList() {
        
        let locationsServerManager = LocationsServerManager()
        locationsServerManager.provideData { (result) in
            self.dataForList = result
            self.tableView.reloadData()
        }
    }
    
    // MARK: - Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.registerNib(UINib(nibName: "ListCell", bundle: nil), forCellReuseIdentifier: "CellInList")

        provideDataForList()
        
    }
    
    // MARK: - Table view delegate methods
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        
        tableView.deselectRowAtIndexPath(indexPath, animated: true)
    }


    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {

        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

        return dataForList.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        if let cell = tableView.dequeueReusableCellWithIdentifier("CellInList", forIndexPath: indexPath) as? ListCell {
            
            cell.listCellTitle.text = dataForList[indexPath.row]["name"] as? String
            return cell
            
        }
        else {
            
            let cell = tableView.dequeueReusableCellWithIdentifier("CellInList", forIndexPath: indexPath)
            return cell
        }
    }

}