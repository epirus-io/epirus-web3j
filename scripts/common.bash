set -eo pipefail

[[ "$TRACE" ]] && set -x

configure_github_user() {
    git config --global user.email "joshua@web3labs.com"
    git config --global user.name "Joshua Richardson"
}

github_clone() {
    git clone https://github.com/epirus-io/$1.git
    cd $1
}

ensure_version() {
    if [[ -z "$VERSION" ]]; then
        VERSION="${TRAVIS_BRANCH//release\/}"
    fi

    if [[ "$VERSION" = "" ]]; then
        echo "ERROR: Missing VERSION specify it using an env variable"
        exit 1
    fi
}

ensure_product() {
    if [[ -z "$PRODUCT" ]]; then
        PRODUCT="${TRAVIS_REPO_SLUG//release\/}"
    fi

    if [[ "$PRODUCT" = "" ]]; then
        echo "ERROR: Missing PRODUCT specify it using an env variable"
        exit 1
    fi
}
