//
//  Regex.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 30.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import Foundation

class Regex: NSObject {
    
    class func check(text: String, with patterns: [String]) -> String {
        var result = text
        for pattern in patterns {
            do {
                let regex = try NSRegularExpression(pattern: pattern)
                let nsString = result as NSString
                let regexResults = regex.matches(in: result, range: NSRange(location: 0, length: nsString.length))
                let regexMatches = regexResults.map {
                    nsString.substring(with: $0.range)
                }
                result = replace(matches: regexMatches, in: result)
                return result
            } catch {
                Log.error(message: "Invalid regex result: \(error.localizedDescription)", sender: self)
            }
        }
        return result
    }
    
    private class func replace(matches: [String], in text: String) -> String {
        var replaced = text
        if matches.count > 0 {
            for match in matches {
                replaced = replaced.replacingOccurrences(of: match, with: "********")
            }
        }
        return replaced
    }

}
