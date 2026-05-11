import fs from "node:fs";
import path from "node:path";

const GH_REPO = "https://github.com/it-at-m/agile-test-automation-framework";

const SITE_HOSTNAME = "https://it-at-m.github.io";
const SITE_BASE = "/agile-test-automation-framework/";

// docs/en/ is the single on-disk source for English content. We use VitePress
// `rewrites` so every docs/en/<path>.md is rendered at URL /<path>.html (the
// root locale). German content lives under docs/de/ and serves under /de/.
const DOCS_DIR = path.resolve(import.meta.dirname, "..");
const EN_DIR = path.join(DOCS_DIR, "en");

const toPosix = (p) => p.split(path.sep).join("/");

const listEnMarkdownFiles = (dir) => {
  const out = [];
  if (!fs.existsSync(dir)) return out;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      out.push(...listEnMarkdownFiles(full));
    } else if (entry.isFile() && entry.name.endsWith(".md")) {
      out.push(full);
    }
  }
  return out;
};

const buildEnRewrites = () => {
  const rewrites = {};
  for (const abs of listEnMarkdownFiles(EN_DIR)) {
    const rel = toPosix(path.relative(EN_DIR, abs));
    rewrites[`en/${rel}`] = rel;
  }
  return rewrites;
};

const navLabels = {
  en: {
    overview: "Overview",
    releases: "Releases",
    openSource: "Open Source",
    digitalAtM: "digital@M",
  },
  de: {
    overview: "Übersicht",
    releases: "Releases",
    openSource: "Open Source",
    digitalAtM: "digital@M",
  },
};

const buildNav = (prefix, lang) => {
  const t = navLabels[lang];
  return [
    { text: t.overview, link: `${prefix}/` },
    { text: t.releases, link: `${GH_REPO}/releases` },
    {
      text: t.openSource,
      link: "https://opensource.muenchen.de/",
    },
    {
      text: t.digitalAtM,
      link: "https://digital-at-m.de/",
    },
  ];
};

const sidebarLabels = {
  en: {
    overview: "Overview",
    introduction: "Introduction",
    projectHistory: "Project History",
    releases: "Releases",
    gettingStarted: "Getting Started",
    prerequisites: "Prerequisites",
    installation: "Installation",
    build: "Build and Integration Tests",
    usage: "Usage",
    writingTests: "Writing Tests",
    runners: "Runners and Running Tests",
    environments: "Environments and Systems",
    configuration: "Configuration",
    properties: "Property Files and Supported Webdrivers",
    credentials: "Runtime Credentials",
    reporting: "Reporting",
    community: "Community",
    roadmap: "Roadmap",
    contributing: "Contributing and License",
    codeOfConduct: "Code of Conduct",
    license: "License (MIT)",
  },
  de: {
    overview: "Übersicht",
    introduction: "Einführung",
    projectHistory: "Projektgeschichte",
    releases: "Releases",
    gettingStarted: "Erste Schritte",
    prerequisites: "Voraussetzungen",
    installation: "Installation",
    build: "Build und Integrationstests",
    usage: "Nutzung",
    writingTests: "Tests schreiben",
    runners: "Runner und Testausführung",
    environments: "Umgebungen und Systeme",
    configuration: "Konfiguration",
    properties: "Property-Dateien und unterstützte Webdriver",
    credentials: "Laufzeit-Zugangsdaten",
    reporting: "Reporting",
    community: "Community",
    roadmap: "Roadmap",
    contributing: "Mitwirken und Lizenz",
    codeOfConduct: "Verhaltenskodex",
    license: "Lizenz (MIT)",
  },
};

const buildSidebar = (prefix, lang) => {
  const t = sidebarLabels[lang];
  return [
    {
      text: t.overview,
      items: [
        { text: t.introduction, link: `${prefix}/` },
        {
          text: t.projectHistory,
          link: `${prefix}/overview/project-history`,
        },
        { text: t.releases, link: `${prefix}/overview/releases` },
      ],
    },
    {
      text: t.gettingStarted,
      items: [
        {
          text: t.prerequisites,
          link: `${prefix}/getting-started/prerequisites`,
        },
        {
          text: t.installation,
          link: `${prefix}/getting-started/installation`,
        },
        {
          text: t.build,
          link: `${prefix}/getting-started/build`,
        },
      ],
    },
    {
      text: t.usage,
      items: [
        {
          text: t.writingTests,
          link: `${prefix}/usage/writing-tests`,
        },
        {
          text: t.runners,
          link: `${prefix}/usage/runners`,
        },
        {
          text: t.environments,
          link: `${prefix}/usage/environments`,
        },
      ],
    },
    {
      text: t.configuration,
      items: [
        {
          text: t.properties,
          link: `${prefix}/configuration/properties`,
        },
        {
          text: t.credentials,
          link: `${prefix}/configuration/credentials`,
        },
      ],
    },
    {
      text: t.reporting,
      link: `${prefix}/reporting`,
    },
    {
      text: t.community,
      items: [
        { text: t.roadmap, link: `${prefix}/community/roadmap` },
        {
          text: t.contributing,
          link: `${prefix}/community/contributing`,
        },
        {
          text: t.codeOfConduct,
          link: `${GH_REPO}/blob/main/CODE_OF_CONDUCT.md`,
        },
        { text: t.license, link: `${GH_REPO}/blob/main/LICENSE` },
      ],
    },
  ];
};

const sharedThemeConfig = {
  socialLinks: [
    {
      icon: "github",
      link: `${GH_REPO}/`,
      ariaLabel: "GitHub Repository",
    },
  ],
  search: {
    provider: "local",
    options: {
      locales: {
        de: {
          translations: {
            button: {
              buttonText: "Suchen",
              buttonAriaLabel: "Suchen",
            },
            modal: {
              displayDetails: "Details anzeigen",
              resetButtonTitle: "Suche zurücksetzen",
              backButtonTitle: "Suche schließen",
              noResultsText: "Keine Ergebnisse",
              footer: {
                selectText: "Auswählen",
                selectKeyAriaLabel: "Eingabe",
                navigateText: "Navigieren",
                navigateUpKeyAriaLabel: "Pfeil nach oben",
                navigateDownKeyAriaLabel: "Pfeil nach unten",
                closeText: "Schließen",
                closeKeyAriaLabel: "esc",
              },
            },
          },
        },
      },
    },
  },
};

const toUrlPath = (relativePath) => {
  let p = (relativePath || "").replace(/\\/g, "/");
  p = p.replace(/^en\//, "");
  p = p.replace(/\.md$/, ".html");
  p = p.replace(/(^|\/)index\.html$/, "$1");
  return p;
};

const stripDeLocalePrefix = (urlPath) => urlPath.replace(/^de\//, "");

const buildAbsoluteUrl = (urlPath) => `${SITE_HOSTNAME}${SITE_BASE}${urlPath}`;

const localeOfUrl = (urlPath) => (urlPath.startsWith("de/") ? "de" : "en");

export default {
  title: "ATAF Docs",
  description:
    "The Agile Test Automation Framework (ATAF) is a test automation framework specifically developed for agile projects. It provides a simple and extensible way to create and manage automated tests with Cucumber in Jira.",
  base: SITE_BASE,
  lang: "en-US",
  rewrites: buildEnRewrites(),
  sitemap: {
    hostname: `${SITE_HOSTNAME}${SITE_BASE}`,
  },
  transformHead({ pageData, siteConfig }) {
    const tags = [];
    const urlPath = toUrlPath(pageData.relativePath);
    const fullUrl = buildAbsoluteUrl(urlPath);
    const stripped = stripDeLocalePrefix(urlPath);
    const enUrl = buildAbsoluteUrl(stripped);
    const deUrl = buildAbsoluteUrl(`de/${stripped}`);
    const locale = localeOfUrl(urlPath);
    const ogLocale = locale === "de" ? "de_DE" : "en_US";

    const fm = pageData.frontmatter || {};
    const siteTitle = siteConfig?.site?.title || "ATAF Docs";
    const siteDescription =
      siteConfig?.site?.description ||
      "The Agile Test Automation Framework (ATAF) is a test automation framework specifically developed for agile projects.";
    const pageTitle = fm.title || pageData.title || siteTitle;
    const pageDescription =
      fm.description || pageData.description || siteDescription;

    tags.push(["link", { rel: "canonical", href: fullUrl }]);
    tags.push(["link", { rel: "alternate", hreflang: "en", href: enUrl }]);
    tags.push(["link", { rel: "alternate", hreflang: "de", href: deUrl }]);
    tags.push([
      "link",
      { rel: "alternate", hreflang: "x-default", href: enUrl },
    ]);

    tags.push(["meta", { property: "og:type", content: "article" }]);
    tags.push(["meta", { property: "og:site_name", content: siteTitle }]);
    tags.push(["meta", { property: "og:title", content: pageTitle }]);
    tags.push([
      "meta",
      { property: "og:description", content: pageDescription },
    ]);
    tags.push(["meta", { property: "og:url", content: fullUrl }]);
    tags.push(["meta", { property: "og:locale", content: ogLocale }]);
    tags.push(["meta", { name: "twitter:card", content: "summary" }]);
    tags.push(["meta", { name: "twitter:title", content: pageTitle }]);
    tags.push([
      "meta",
      { name: "twitter:description", content: pageDescription },
    ]);

    return tags;
  },
  markdown: {
    config(md) {
      const defaultFence = md.renderer.rules.fence;
      md.renderer.rules.fence = (tokens, idx, options, env, self) => {
        const token = tokens[idx];
        const info = md.utils.unescapeAll(token.info || "").trim();
        const langName = info.split(/\s+/g)[0];
        if (langName === "mermaid") {
          return `<pre class="mermaid" v-pre>${md.utils.escapeHtml(token.content)}</pre>`;
        }
        if (defaultFence) {
          return defaultFence(tokens, idx, options, env, self);
        }
        return `<pre><code>${md.utils.escapeHtml(token.content)}</code></pre>`;
      };
    },
  },
  head: [
    [
      "link",
      {
        rel: "icon",
        href: "https://assets.muenchen.de/logos/lhm/icon-lhm-muenchen-32.png",
      },
    ],
    ["meta", { name: "robots", content: "index,follow" }],
    ["meta", { name: "author", content: "it@M / Landeshauptstadt München" }],
    [
      "meta",
      {
        name: "keywords",
        content:
          "ATAF, Agile Test Automation Framework, java, government, testing, jira, agile, maven, selenium, test automation, cucumber, testing tools, municipalities, jira rest api, restassured, municipality, municipals, restassured framework, jira integration, digital@M, it@M, it-at-m, Munich, München, open source, TestNG, JUnit, BDD, Xray",
      },
    ],
  ],
  themeConfig: {
    ...sharedThemeConfig,
  },
  locales: {
    root: {
      label: "English",
      lang: "en",
      themeConfig: {
        ...sharedThemeConfig,
        nav: buildNav("", "en"),
        sidebar: buildSidebar("", "en"),
      },
    },
    de: {
      label: "Deutsch",
      lang: "de",
      link: "/de/",
      title: "ATAF-Doku",
      description:
        "Das Agile Test Automation Framework (ATAF) ist ein speziell für agile Projekte entwickeltes Testautomatisierungs-Framework. Es bietet eine einfache und erweiterbare Möglichkeit, automatisierte Tests mit Cucumber in Jira zu erstellen und zu verwalten.",
      themeConfig: {
        ...sharedThemeConfig,
        nav: buildNav("/de", "de"),
        sidebar: buildSidebar("/de", "de"),
        outline: { label: "Auf dieser Seite", level: [2, 4] },
        darkModeSwitchLabel: "Darstellung",
        langMenuLabel: "Sprache wechseln",
        returnToTopLabel: "Zurück nach oben",
        sidebarMenuLabel: "Menü",
        docFooter: { prev: "Vorherige Seite", next: "Nächste Seite" },
        lastUpdatedText: "Zuletzt aktualisiert",
      },
    },
  },
};
