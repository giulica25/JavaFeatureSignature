from datetime import datetime, timedelta, timezone
import json
import numpy as np
import os

from matplotlib.colors import LinearSegmentedColormap
import matplotlib.pyplot as plt
from scipy import stats

directory = os.fsencode("results/")

feature_names = [
    "INNER_CLASS",
    "STRICTFP",
    "SWITCH_STATEMENT",
    "ASSERT",
    "ANNOTATION",
    # "ENUM",
    "GENERIC_CLASS",
    "GENERIC_METHOD",
    "FOR_EACH",
    "STATIC_IMPORT_DECLARATION",
    "VARARGS",
    "BINARY_BOOLEAN_LITERAL_EXPRESSION",
    "BINARY_DOUBLE_LITERAL_EXPRESSION",
    "BINARY_INTEGER_LITERAL_EXPRESSION",
    "BINARY_LONG_LITERAL_EXPRESSION",
    "CATCH_MULTIPLE_EXCEPTION_TYPES",
    "DIAMOND_OPERATOR",
    "STRING_SWITCH",
    "TRY_WITH_RESOURCES",
    "UNDERSCORE_IN_DOUBLE_LITERAL",
    "UNDERSCORE_IN_INTEGER_LITERAL",
    "UNDERSCORE_IN_LONG_LITERAL",
    "INTERFACE_DEFAULT_METHOD",
    "INTERFACE_STATIC_METHOD",
    "LAMBDA_EXPRESSION",
    "METHOD_REFERENCE_EXPRESSION",
    "OPTIONAL_VARIABLE_DECLARATION",
    "REPEATABLE_ANNOTATION",
    "TYPE_ANNOTATION",
    "INTERFACE_PRIVATE_METHOD",
    "SAFE_VARARGS",
    "TRY_WITH_RESOURCES_FINAL_VARIABLE",
    "VARHANDLE",
    "VAR_TYPE",
    "LAMBDA_VAR_TYPE",
    "SWITCH_EXPRESSION",
    "SWITCH_ENTRY_MULTIPLE_LABELS",
    "SWITCH_ENTRY_TYPE_EXPRESSION",
    "YIELD",
    "TEXT_BLOCK_LITERAL_EXPRESSION",
    "TEXT_BLOCK_ESCAPE_NEWLINE",
    "TEXT_BLOCK_ESCAPE_SPACE",
    "INSTANCE_OF_PATTERN",
    "RECORD",
    "LOCAL_RECORD",
    "SEALED_MODIFIER",
    "FOR_EACH_RECORD_PATTERN_EXPRESSION",
    "INSTANCEOF_RECORD_PATTERN_EXPRESSION",
    "SWITCH_EXPRESSION_GUARD",
    "SWITCH_EXPRESSION_PATTERN_MATCHING",
    "SWITCH_EXPRESSION_RECORD_PATTERN_EXPRESSION",
    "SWITCH_STATEMENT_GUARD",
    "SWITCH_STATEMENT_PATTERN_MATCHING",
    "SWITCH_STATEMENT_RECORD_PATTERN_EXPRESSION",
    "UNDERSCORE",
    "SWITCH_STATEMENT_PRIMITIVE_TYPE_PATTERN_EXPRESSION",
    "SWITCH_EXPRESSION_PRIMITIVE_TYPE_PATTERN_EXPRESSION",
    "INSTANCE_OF_PATTERN_PRIMITIVE_TYPE",
    # "UNNAMED_CLASS",
    "FLEXIBLE_CONSTRUCTOR_BODIES",
    "INSTANCE_MAIN_METHOD",
    "MODULE_IMPORT_DECLARATION",
    "NON_PUBLIC_MAIN_METHOD",
    "STABLE_VALUE"
]

tagged_repos = {
    "krahets/hello-algo": ["alg"],
    "iluwatar/java-design-patterns": ["alg"],
    "macrozheng/mall": ["app"],
    "spring-projects/spring-boot": ["app", "lib"],
    "elastic/elasticsearch": ["app"], # lib?
    "NationalSecurityAgency/ghidra": ["app"],
    "TheAlgorithms/Java": ["alg"],
    "spring-projects/spring-framework": ["app", "lib"],
    "termux/termux-app": ["app"],
    "google/guava": ["app", "lib"],
    "dbeaver/dbeaver": ["app"],
    "skylot/jadx": ["app"],
    "ReactiveX/RxJava": ["lib"],
    "jeecgboot/JeecgBoot": ["app"], # Generates a Java app from a description using AI
    "apache/dubbo": ["app"], # Java implementation of an RPC framework
    "halo-dev/halo": ["app"], # Website building tool
    "TeamNewPipe/NewPipe": ["app"], # Cracked YouTube app for Android
    "ashishps1/awesome-system-design-resources": ["alg"],
    "eugenp/tutorials": ["alg"],
    "alibaba/arthas": ["lib"], # Java diagnostic tool
    "YunaiV/ruoyi-vue-pro": ["lib"], # China's premier developer platform?
    "doocs/leetcode": ["alg"], # Leetcode solutions in multiple programming languages
    "airbnb/lottie-android": ["lib"], # Renders Adobe AE animations on Android
    "bumptech/glide": ["lib"], # Android image library
    "netty/netty": ["lib"], # App framework
    "keycloak/keycloak": ["app", "lib"], # App that manages authentication for an app you write?
    "SeleniumHQ/selenium": ["app", "lib"], # Browser automation framework?
    "zxing/zxing": ["lib"], # Barcode scanning library
    "alibaba/nacos": ["app"],
    "binarywang/WxJava": ["lib"], # WeChat Java SDK
    "apache/kafka": ["app"], # Distributed event streaming platform
    "conductor-oss/conductor": ["app"],
    "chinabugotech/hutool": ["lib"],
    "yuliskov/SmartTube": ["app"],
    "xuxueli/xxl-job": ["lib"],
    "apolloconfig/apollo": ["app"],
    "alibaba/canal": ["app", "lib"],
    "DrKLO/Telegram": ["app"],
    "alibaba/spring-cloud-alibaba": ["lib"],
    "alibaba/druid": ["lib"],
    "Anuken/Mindustry": ["app"],
    "kestra-io/kestra": ["app"],
    "OpenAPITools/openapi-generator": ["app"],
    "apache/flink": ["lib"],
    "apache/incubator-seata": ["lib"],
    "CodePhiliaX/Chat2DB": ["app"],
    "bazelbuild/bazel": ["app"],
    "jenkinsci/jenkins": ["app"],
    "libgdx/libgdx": ["lib"],
    "apache/skywalking": ["app"],
    "iBotPeaches/Apktool": ["app"],
    "Netflix/Hystrix": ["lib"],
    "redisson/redisson": ["lib"],
    "google/gson": ["lib"],
    "ashishps1/awesome-low-level-design": ["alg"],
    "dataease/dataease": ["app"],
    "alibaba/Sentinel": ["lib"],
    "openjdk/jdk": ["lib"],
    "apache/rocketmq": ["app"],
    "elunez/eladmin": ["app"],
    "thingsboard/thingsboard": ["app"],
    "oracle/graal": ["app"],
    "opendataloader-project/opendataloader-pdf": ["app"],
    "CarGuo/GSYVideoPlayer": ["lib"], #Not sure
    "apache/shardingsphere": ["app"],
    "mybatis/mybatis-3": ["lib"], # SQL mapper framework
    "JetBrains/intellij-community": ["app"],
    "ReactiveX/RxAndroid": ["lib"],
    "lionsoul2014/ip2region": ["lib"],
    "YunaiV/yudao-cloud": ["lib"], # See ruoyi
    "antlr/antlr4": ["lib"], # Parsing library
    "dromara/Sa-Token": ["lib"], # Authentication framework (maybe app?)
    "williamfiset/Algorithms": ["alg"],
    "Tencent/APIJSON": ["lib"], # ORM library
    "ben-manes/caffeine": ["lib"],
    "Tencent/tinker": ["lib"],
    "infinilabs/analysis-ik": ["lib"], # Plugin for Elasticsearch and OpenSearch (app?)
    "openzipkin/zipkin": ["app", "lib"],
    "baomidou/mybatis-plus": ["lib"], # MyBatis toolkit
    "material-components/material-components-android": ["lib"],
    "facebook/fresco": ["lib"], # Library for managing images
    "itwanger/toBeBetterJavaer": ["alg"],
    "questdb/questdb": ["app"], # Time series database
    "prestodb/presto": ["app"], # Distributed SQL query engine
    "ashishps1/awesome-leetcode-resources": ["alg"],
    "quarkusio/quarkus": ["lib"],
    "apache/hadoop": ["lib"],
    "Konloch/bytecode-viewer": ["app"], # APK reverse-engineering tool
    "mockito/mockito": ["lib"], # Mocking framework for unit tests
    "apache/doris": ["app"], # Analytics database
    "apache/pulsar": ["app"], # Distributed pub-sub messaging platform
    "zaproxy/zaproxy": ["app"], # Web app scanner (maybe lib?)
    "cryptomator/cryptomator": ["app"], # Client-side encryption of cloud files
    "supertokens/supertokens-core": ["lib"], # Authentication framework
    "zhisheng17/flink-learning": ["lib"],
    "theonedev/onedev": ["app"], # GitHub alternative
    "elastic/logstash": ["lib"],
    "eclipse-vertx/vert.x": ["lib"],
    "languagetool-org/languagetool": ["app"], # Style/grammar analysis for natural language
    "GoogleContainerTools/jib": ["lib"], # Tool to containerize Java apps (maybe app?)
    "apache/dolphinscheduler": ["app"], # Orchestration platform
    "deeplearning4j/deeplearning4j": ["lib"], # Deep learning libraries for Java
    "kekingcn/kkFileView": ["app"], # Online file preview
    "xpipe-io/xpipe": ["app"], # Remote connection to infrastructure
    "Netflix/zuul": ["app"],
    "apache/druid": ["app"], # Analytics database
    "pinpoint-apm/pinpoint": ["app"],
    "projectlombok/lombok": ["lib"], # Adds features to Java
    "microg/GmsCore": ["lib"], # Play Services emulator
    "metersphere/metersphere": ["app"], # AI testing tool
    "plantuml/plantuml": ["app"],
    "opensearch-project/OpenSearch": ["app"],
    "floci-io/floci": ["app", "lib"], # Local AWS emulator
    "macrozheng/mall-swarm": ["app"], # Microservice e-commerce system
    "LawnchairLauncher/lawnchair": ["app"], # Android
    "trinodb/trino": ["app"], # SQL query engine
    "codecentric/spring-boot-admin": ["app"],
    "apache/zookeeper": ["app"],
    "debezium/debezium": ["lib"], # Database monitoring library?
    "google/guice": ["lib"], # Dependency injection framework
    "Netflix/eureka": ["app"],
    "beemdevelopment/Aegis": ["app"], # Authenticator app
    "pagehelper-org/Mybatis-PageHelper": ["lib"], # MyBatis pagination plugin
    "redis/jedis": ["lib"], # Redis client
    "PaperMC/Paper": ["app"], # Minecraft server
    "langchain4j/langchain4j": ["lib"], # Library for LLM apps
    "grpc/grpc-java": ["lib"], # RPC library
    "OpenRefine/OpenRefine": ["app"], # Web app for cleaning up data
    "jd-opensource/joyagent-jdgenie": ["app"], # AI agents
    "StarRocks/starrocks": ["app"], # Database
    "jhy/jsoup": ["lib"], # HTML parser
    "apple/pkl": ["app"], # Programming language (maybe lib?)
    "apereo/cas": ["app", "lib"], # Central Authentication Service
    "JingMatrix/Vector": ["lib"],
    "daniulive/SmarterStreaming": ["lib"], # Video streaming SDK
    "jwtk/jjwt": ["lib"],
    "asLody/VirtualApp": ["lib"],
    "clojure/clojure": ["app"], # Programming language (maybe lib?)
    "TooTallNate/Java-WebSocket": ["lib"],
    "zfile-dev/zfile": ["app"], # File storage
    "resilience4j/resilience4j": ["lib"], # Fault tolerance library for functional programming
}


def load_repo_data(path, mode="normalized"):
    with open(path) as file:
        data = json.load(file)

    name = data["full_name"]
    commits = data["commits"]

    today = datetime.now(timezone.utc)
    month = (today.year, today.month)

    i = 0
    feature_usage = []
    while i < len(data["commits"]):
        commit = commits[i]
        commit_date = datetime.fromisoformat(commit["date"])
        commit_month = (commit_date.year, commit_date.month)

        if month > commit_month:
            feature_usage.append([np.nan] * len(feature_names))
        else:
            if commit["feature_usage"] is not None and commit["total_size"] != 0:
                match mode:
                    case "normalized":
                        feature_usage.append([commit["feature_usage"][feature] / commit["total_size"] for feature in feature_names])
                    case "binary":
                        feature_usage.append([1 if commit["feature_usage"][feature] else 0 for feature in feature_names])

            else:
                feature_usage.append([np.nan] * len(feature_names))
            i += 1

        month = (month[0], month[1] - 1)
        if month[1] == 0:
            month = (month[0] - 1, 12)

    return name, np.array(feature_usage)


def make_usage_plot():
    fig, ax = plt.subplots(figsize=(10, 8))

    cmap = LinearSegmentedColormap.from_list(
        "PwP",
        [
            "#94245E",
            "#F7DEEB"
        ]
    )

    im = ax.imshow(averaged_binary_data, cmap=cmap, vmin=0, vmax=1)

    latest_month = datetime.fromisoformat("2026-05-15")
    x_labels = [(latest_month - timedelta(days=30.437) * i).strftime("%b %Y") for i in range(averaged_binary_data.shape[1] - 1, -1, -1)]
    ax.set_xticks(range(averaged_binary_data.shape[1]), labels=x_labels,  rotation=45, ha="right", rotation_mode="anchor", fontsize=3.7)
    ax.set_yticks(range(len(feature_names)), labels=feature_names, fontsize=275/len(feature_names))

    ax.set_xticks(np.arange(-0.5, averaged_binary_data.shape[1], 1), minor=True)
    ax.set_yticks(np.arange(-0.5, averaged_binary_data.shape[0], 1), minor=True)

    ax.grid(which="minor", color="white", linestyle="-", linewidth=(20/len(averaged_binary_data[:,1])))
    fig.colorbar(im, ax=ax, label="Usage", shrink=0.5)

    fig.tight_layout()
    plt.savefig(f"plots/usage.svg", dpi=600, bbox_inches="tight")


def correlation(array1, array2):
    rows = min(len(array1), len(array2))
    columns = array1.shape[1]

    correlations = []
    for i in range(columns):
        column1 = np.take(array1, indices=i, axis=1)
        column2 = np.take(array2, indices=i, axis=1)

        kept_indices = [index for index in range(rows) if not np.isnan(column1[index]) and not np.isnan(column2[index])]
        if len(kept_indices) < 2:
            continue
        column1 = np.take(column1, indices=kept_indices)
        column2 = np.take(column2, indices=kept_indices)

        res = stats.pearsonr(column1, column2)
        if not np.isnan(res.statistic):
            correlations.append(res.statistic)
    if not correlations:
        return np.nan
    return sum(correlations) / len(correlations)


def make_correlation_plot(array, labels, name):
    raw_correlations = []
    not_all_nan_indices = []
    for i, x in enumerate(array):
        row = []
        all_nan = True
        for y in array:
            c = correlation(x, y)
            row.append(c)
            if not np.isnan(c):
                all_nan = False
        raw_correlations.append(row)
        if not all_nan:
            not_all_nan_indices.append(i)
        else:
            print(f"Removing {labels[i]} from correlation heat map due to all-NaN correlation values.")

    correlations = np.array(raw_correlations)
    correlations = np.take(np.array(correlations), indices=not_all_nan_indices, axis=0)
    correlations = np.take(np.array(correlations), indices=not_all_nan_indices, axis=1)
    labels = [labels[i] for i in not_all_nan_indices]

    fig, ax = plt.subplots(figsize=(10, 8))

    cmap = LinearSegmentedColormap.from_list(
        "PwP",
        [
            "#4B5E81",
            "#E0E0E0",
            "#B03077"
        ]
    )

    im = ax.imshow(correlations, cmap=cmap, vmin=-1, vmax=1)

    fig.colorbar(im, ax=ax, label="Correlation")

    ax.set_xticks(range(len(labels)), labels=labels,
              rotation=45, ha="right", rotation_mode="anchor", fontsize=(100/len(labels)))
    ax.set_yticks(range(len(labels)), labels=labels, fontsize=(100/len(labels)))


    ax.set_xticks(np.arange(-0.5, len(labels), 1), minor=True)
    ax.set_yticks(np.arange(-0.5, len(labels), 1), minor=True)
    ax.grid(which="minor", color="white", linestyle="-", linewidth=(20/len(labels)))

    ax.tick_params(which="minor", bottom=False, left=False)

    fig.tight_layout()
    plt.savefig(f"plots/{name}_correlation.svg", dpi=600, bbox_inches="tight")


def repo_indices_by_tag(tag):
    return [i for i in range(len(repos)) if tag in tagged_repos[repos[i]]]


repos = []
data = []
binary_data = []

for file in os.listdir(directory):
    file_name = os.fsdecode(file)
    if file_name.endswith(".json"):
        file_path = os.path.join(directory, file)
        name, commits = load_repo_data(file_path)
        if np.isnan(commits).all():
            print(f"Removed repo {name} due to all-NaN values.")
            continue
        repos.append(name)
        data.append(commits)

        name, binary_commits = load_repo_data(file_path, mode="binary")
        binary_data.append(binary_commits)

max_count = max(len(commits) for commits in data)
data = np.array([np.pad(commits, [(0, max_count - len(commits)), (0, 0)], mode="constant", constant_values=np.nan) for commits in data])
binary_data = np.array([np.pad(commits, [(0, max_count - len(commits)), (0, 0)], mode="constant", constant_values=np.nan) for commits in binary_data])
averaged_binary_data = np.take(np.transpose(np.nanmean(binary_data, axis=0)), indices=range(85, 0, -1), axis=1)

make_usage_plot()

for tag in ["alg", "app", "lib"]:
    filtered_indices = repo_indices_by_tag(tag)
    filtered_data = np.take(data, indices=filtered_indices, axis=0)
    filtered_repos = [repos[i] for i in filtered_indices]

    make_correlation_plot(filtered_data, filtered_repos, f"{tag}_repo")

    make_correlation_plot(np.transpose(filtered_data, axes=(2, 1, 0)), feature_names, f"{tag}_feature")
