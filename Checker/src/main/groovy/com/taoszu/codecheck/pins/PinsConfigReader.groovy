
package com.taoszu.codecheck.pins

import org.gradle.api.Project


class PinsConfigReader {

    static final Map<String, Set<String>> pinsDenpendMap = new HashMap<>()

    static void read(Project project, String path) {
        def config = new XmlSlurper().parse(new File("/Users/hao/Downloads/PinsFrameChecker/app/src/p_shop/p_dependencies.xml"))

        String pinsSrc = config.pins_src.name[0].text()

        Set<String> dependSet = new HashSet<>()
        for (name in config.pins_depend.name) {
            dependSet.add(name.text())
        }

        pinsDenpendMap.put(pinsSrc, dependSet)
        //project.logger.error( "pins : " + pinsDenpendMap.toString())
    }


}
