import requests
from datetime import datetime, timezone
import json
import time

PAGES = 5
MIN_JAVA_BYTES = 16384
MAX_DAYS_SINCE_LAST_COMMIT = 183
GITHUB_TOKEN = None

url = "https://api.github.com/search/repositories"
headers = {
    "Accept": "application/vnd.github+json"
}
if GITHUB_TOKEN:
    headers["Authorization"] = f"Bearer {GITHUB_TOKEN}"

unfiltered_repos = []
for i in range(1, PAGES + 1):
    time.sleep(0.5)

    # Search filters
    params = {
        "q": "language:java stars:>1000 archived:false",
        "sort": "stars",
        "order": "desc",
        "per_page": 100,
        "page": i,
    }

    # Get repository list
    response = requests.get(url, headers=headers, params=params)
    response_data = response.json()
    response_repos = response_data["items"]
    print(f"Received page {i} containing {len(response_repos)} repos.")
    unfiltered_repos.extend(response_repos)

print(f"Received {len(unfiltered_repos)} repos over {PAGES} pages. Starting filtering proces...")

# Filter repositories by minimum Java percentage
filtered_repos = []
for repo in unfiltered_repos:
    time.sleep(0.5)

    full_name = repo["full_name"]
    languages_url = repo["languages_url"]
    languages_response = requests.get(languages_url, headers=headers)
    if languages_response.status_code != 200:
        print(f"Repo {full_name} skipped due to failed request to languages endpoint!")
        continue
    languages_data = languages_response.json()
    java_bytes = languages_data.get("Java", 0)

    if java_bytes < MIN_JAVA_BYTES:
        print(f"Repo {full_name} skipped due to insufficient Java code ({java_bytes} bytes)!")
        continue

    default_branch = repo["default_branch"]
    default_branch_url = f"{repo["url"]}/branches/{default_branch}"
    default_branch_response = requests.get(default_branch_url, headers=headers)
    if default_branch_response.status_code != 200:
        print(f"Repo {full_name} skipped due to failed default branch request!")
        continue
    default_branch_data = default_branch_response.json()
    last_commit_time_string = default_branch_data["commit"]["commit"]["committer"]["date"]
    last_commit_time = datetime.fromisoformat(last_commit_time_string)
    current_time = datetime.now(timezone.utc)
    time_since_last_commit = current_time - last_commit_time

    if time_since_last_commit.days > MAX_DAYS_SINCE_LAST_COMMIT:
        print(f"Repo {full_name} skipped due to latest commit being too old ({time_since_last_commit.days} days)!")
        continue

    filtered_repos.append(repo)

with open("repos.json", "w") as file:
    file.write(json.dumps(filtered_repos, indent=2))
