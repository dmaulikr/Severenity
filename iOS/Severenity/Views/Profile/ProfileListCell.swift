//
//  ListCell.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 13.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileListCell: UITableViewCell {
    
    @IBOutlet weak var listCellTitle: UILabel!
    
    // MARK: - Init
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        listCellTitle.text = ""
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

}
