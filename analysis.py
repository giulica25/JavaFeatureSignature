from git import Repo  # pip install gitpython
import json
import glob
import os
import shutil
from datetime import datetime, timezone

REPO_START = None
REPO_END = None
COMMAND = "command to run Main.java"

os.makedirs("results", exist_ok=True)

with open("repos.json") as file:
    repos = json.load(file)

start = 0 if REPO_START is None else REPO_START
end = len(repos) if REPO_END is None else REPO_END
for repo_index in range(start, end):
    repo = repos[repo_index]
    full_name = repo["full_name"]
    clone_url = repo["clone_url"]

    print(f"Cloning repo #{repo_index}: {full_name}")
    repo = Repo.clone_from(clone_url, "tmp/clone", single_branch=True)

    print(f"Analyzing repo #{repo_index}: {full_name}")
    commit_results = []
    last_processed_month = None
    for commit_index, commit in enumerate(repo.iter_commits()):
        commit_date = datetime.fromtimestamp(commit.committed_date, timezone.utc)
        commit_month = (commit_date.year, commit_date.month)
        if last_processed_month is not None and commit_month == last_processed_month:
            continue

        print(f"Analyzing commit #{commit_index}: {commit.hexsha}")
        repo.git.checkout(commit, force=True)
        os.system(f"{COMMAND} tmp/clone tmp/result")
        try:
            with open("tmp/result", "r") as file:
                commit_feature_usage = json.load(file)
            os.remove("tmp/result")
        except FileNotFoundError:
            commit_feature_usage = None
        java_file_paths = glob.glob("tmp/clone/**/*.java", recursive=True)
        total_size = sum(os.path.getsize(p) for p in java_file_paths)

        commit_result = {
            "hexsha": commit.hexsha,
            "date": commit_date.isoformat(),
            "feature_usage": commit_feature_usage,
            "total_size": total_size,
        }
        commit_results.append(commit_result)
        last_processed_month = commit_month

    print(f"Deleting repo {full_name}")
    shutil.rmtree("tmp/clone")

    repo_result = {
        "full_name": full_name,
        "clone_url": clone_url,
        "commits": commit_results,
    }
    with open(f"results/{repo_index}.json", "w+") as file:
        file.write(json.dumps(repo_result, indent=2))

    print(f"Result saved at results/{repo_index}.json\n")
