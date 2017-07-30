# Family Tree


Example of an app using yFiles(yWorks) for Java library to create dynamically a family tree graph.


![Tree Example](https://k60.kn3.net/B/E/7/4/A/6/5C2.png)


##

It uses the  FamilyTreeLayout algorithm which can only handle specific graph structures as described in section Concept http://docs.yworks.com/yfilesjava/doc/api/#/api/com.yworks.yfiles.layout.genealogy.FamilyTreeLayout.
Each node has to be mapped to a type, i.e., family or individual (male/female) where family nodes can have at most two incoming edges from the parents and there are no edges between nodes of the same type.



## Usage

In order to run the program you need to create an account and get the licence file on yWorks site
https://www.yworks.com/products/yfiles-for-java-2.x/evaluate

Then put the license file inside the src/ folder


## Contributing

1. Fork it ( https://github.com/r-deleon/familyTree )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create a new Pull Request


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
