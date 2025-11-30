{
  "branches": [
    "main",
    "develop"
  ],
  "plugins": [
    [
      "@semantic-release/commit-analyzer",
      {
        "preset": "angular",
        "parserOpts": {
          "noteKeywords": [
            "BREAKING CHANGE",
            "BREAKING CHANGES",
            "BREAKING"
          ]
        }
      }
    ],
    [
      "@semantic-release/github",
      {
        "assets": [
          "dist/**",
          "build/**",
          "public/**"
        ],
        "releasedLabels": [
          "Status: Released"
        ],
        "releasedMilestones": "all",
        "releasedLabels": [
          "Status: Released"
        ]
      }
    ],
    [
      "@semantic-release/release-notes-generator",
      {
        "preset": "angular",
        "parserOpts": {
          "noteKeywords": [
            "BREAKING CHANGE",
            "BREAKING CHANGES",
            "BREAKING"
          ]
        },
        "writerOpts": {
          "commitsSort": [
            "subject",
            "scope"
          ]
        }
      }
    ]
  ],
  "tagFormat": "v${version}"
}