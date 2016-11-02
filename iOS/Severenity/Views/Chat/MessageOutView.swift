//
//  MessageView.swift
//  Severenity
//
//  Created by Yura Yasinskyy on 29.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class MessageOutView: UITableViewCell {
    
    @IBOutlet weak var profilePicture: UIImageView!
    @IBOutlet weak var messageText: UITextView!
    @IBOutlet weak var infoLabel: UILabel!

    // MARK: Init
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
}
