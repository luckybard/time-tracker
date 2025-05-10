package com.github.luckybard.timetracker.service;

import com.esotericsoftware.kryo.kryo5.util.Null;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(Service.Level.PROJECT)
public final class GitService {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);

    private final GitRepositoryManager gitRepositoryManager;

    public GitService(@Null Project project) {
        this.gitRepositoryManager = GitRepositoryManager.getInstance(project);
    }

    public String fetchCurrentBranch() {
        logger.info("GitService::fetchCurrentBranch()");
        GitRepository gitRepository = gitRepositoryManager.getRepositories().stream()
                .findFirst()
                .orElse(null);

        if (gitRepository != null && gitRepository.getCurrentBranch() != null) {
            return gitRepository.getCurrentBranch().getName();
        }
        return StringUtils.EMPTY;
    }
}
