/*
 * Copyright (C) 2015 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.collect.HashMultimap
import com.google.common.collect.SetMultimap
import org.artifactory.repo.RepoPath

replication {
    beforeFileReplication { localRepoPath ->
        // List of repos to filter. Any others will be unfiltered.
        def reposToFilter = ["libs-releases-local"]
        SetMultimap<String, String> propsToCheck = HashMultimap.create()
        // List of properties to check. If any are set, the file will be replicated.
        propsToCheck.put("foo", "true")

        if (reposToFilter.contains(localRepoPath.repoKey)) {
            def props = repositories.getProperties(localRepoPath)
            // Check if any property matches.
            def propMatch = false
            props.entries().each{ entry ->
                if (propsToCheck.containsEntry(entry.key, entry.value)) {
                    propMatch = true
                }
            }
            if (propMatch) {
                log.info("Replicating file: ${localRepoPath.getPath()} as it has" +
                     " the right property")
                skip = false
            } else {
                log.info("Skipping replication of a file:" +
                        " ${localRepoPath.getPath()} as it does not have the" +
                         " right property")
                skip = true
            }
        } else {
            // Don't skip if we aren't filtering this repo.
            skip = false
        }
    }
}
