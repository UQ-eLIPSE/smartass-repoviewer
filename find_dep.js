/**
 * A nodejs script to find all unused dependencies within the repo viewer
 * Run 'node find_dep.js' to run
 */

var fs = require("fs");
var path = require("path");

var root = "src/";

function find_files(dir) {
    var files = fs.readdirSync(dir);
    var javaFiles = []
    for (file of files) {
        var loc = path.join(dir, file);
        if (fs.statSync(loc).isDirectory()) {
            javaFiles = javaFiles.concat(find_files(loc));
        } else {
            if (file.indexOf(".java") > -1) {
                javaFiles.push(loc);
            }
        }
    }

    return javaFiles;
}

function process_file(fname) {
    var imports = [];
    var data = fs.readFileSync(fname, 'utf8');
    var lines = data.split("\n");
    for (line of lines) {
        if (line.search(/^import au.edu.uq.smartassrepoeditor/) > -1) {
            if (line.search("\\*") == -1) {
                var clean = line.replace("import au.edu.uq.smartassrepoeditor.", "");
                clean = clean.replace(";", "");
                clean = clean.replace("\r", "");
                imports.push(clean);
            }
        }
    }
    return imports;
}

function process_all(fnames) {
    var imports = [];
    for (var fname of fnames) {
        imports = imports.concat(process_file(fname));
    }
    return imports;
}

function process_file_names(fnames) {
    for (var i = 0; i < fnames.length; i++) {
        var name = fnames[i];
        name = name.replace(/\\/g, ".");
        name = name.replace("src.main.java.au.edu.uq.smartassrepoeditor.", "");
        name = name.replace(/\.java$/, "");
        fnames[i] = name;
    }
}

function check_unused(imports, all) {
    for (var imp of imports) {
        if (all.indexOf(imp) == -1) {
            console.log("Unused: " + imp);
        }
    }
}

var allFiles = find_files(root);
var imports = process_all(allFiles);
process_file_names(allFiles);
check_unused(imports, allFiles);
