#!/usr/bin/env node
const ChartjsNode = require('chartjs-node');
const _ = require('lodash');
const fs = require('fs');

let complex_data = JSON.parse(fs.readFileSync('./complex.json', 'utf8'));
let social_data = JSON.parse(fs.readFileSync('./social.json', 'utf8'));
let complex_tests = ["/users/newUser", "/users/removeUser", "/users/newFacebookUser", "/users/superUser", "/products/buy/productA", "/health", "/cart/cartA/remove", "/cart/cartB/modify"];
let social_tests = ["/feed", "/users/popular", "/users/user1", "/users/user1/events", "/users/user1/likes", "/users/user1/pages", "/users/user1/friends", "/users/user1/feed", "/posts/popular", "/posts/post1", "/posts/post1/tagged", "/posts/post1/photos", "/posts/post1/photos/photo1", "/events/popular", "/events/event1", "/events/event1/partecipants", "/events/event1/invited", "/events/event1/feed", "/pages/popular", "/pages/page1", "/pages/page1/likes", "/pages/page1/events", "/pages/page1/feed", "/pages/page1/feed/post1"];

function runRegex(regexp, s) {
    let a = regexp.exec(s);
    let r = undefined;
    if (!_.isEmpty(a))
        r = a[1];
    regexp.lastIndex = 0;
    return r;
}

function buildChartOptionsForTestsWithGroups(data, tests, groups) {
    let datasets = [];

    for (let group of groups) {
        // Score
        let chartData =
            data
                .filter((value) => runRegex(group.regexp, value.benchmark) != undefined)
                .map((value) => ({
                    index: parseInt(runRegex(group.regexp, value.benchmark)),
                    score: value.primaryMetric.score,
                    rawData: _.flatten(value.primaryMetric.rawData)
                }));
        chartData.sort((a, b) => a.index - b.index);

        datasets.push({
            label: group.label,
            backgroundColor: group.mainColor,
            borderColor: group.mainColor,
            fill: false,
            data: chartData.map((value) => (group.scale) ? (value.score * group.scale) : value.score)
        });

        // Raw Data
        for (let i = 0; i < chartData[0].rawData.length; i++) {
            datasets.push({
                label: group.label + "-Iteration" + (i + 1),
                backgroundColor: group.transparentColor,
                borderColor: group.transparentColor,
                fill: false,
                data: chartData.map((value) => (group.scale) ? (value.rawData[i] * group.scale) : value.rawData[i])
            });
        }
    }

    let allowedLabels = groups.map((group) => group.label);

    return {
        type: 'line',
        data: {
            labels: tests,
            datasets: datasets
        },
        options: {
            showLines: true,
            lineTension: 0,
            scales: {
                xAxes: [{
                    ticks: {
                        fontSize: 12,
                        display: true,
                        autoSkip: false
                    }
                }]
            },
            legend: {
                display: true,
                labels: {
                    filter: (l) => allowedLabels.includes(l.text)
                }
            }
        }
    }
}

function buildChartOptionsForAverage(data, datasets) {
    let obj = {
        type: 'bar',
        data: {
            labels: [],
            datasets: [
                {
                    label: "Values",
                    backgroundColor: [],
                    data: []
                }
            ]
        },
        options: {
            legend: {
                display: false
            },
            scales: {
                xAxes: [{
                    ticks: {
                        fontSize: 12,
                        display: true,
                        autoSkip: false
                    }
                }]
            }
        }
    };
    for (let set of datasets) {
        obj.data.labels.push(set.label);
        obj.data.datasets[0].backgroundColor.push(set.color);
        obj.data.datasets[0].data.push(data.find((el) => el.benchmark == set.name).primaryMetric.score);
    }
    return obj;
}

function doChart(length, height, chartOptions, imageName) {
    let chartNode = new ChartjsNode(length, height);
    chartNode.drawChart(chartOptions)
        .then(buffer => {
            return chartNode.getImageStream('image/png');
        }).then(streamResult => {
        return chartNode.writeImageToFile('image/png', imageName);
    }).catch(err => {
        console.error(err)
    });
}

//doChart(400, 800, buildChartOptionsForAverage(complex_data, "io.slinkydeveloper.bench.ComplexRegexBenchmark.treeRouting", "io.slinkydeveloper.bench.ComplexRegexBenchmark.skipListRouting", "Tree", "List"), "complex_average.png");
doChart(400, 800, buildChartOptionsForAverage(social_data,
    [
        {
            "label": "Tree",
            "name": "io.slinkydeveloper.bench.SocialNetworkBenchmark.treeRouting",
            "color": "rgba(255, 0, 0, 1)"
        },
        {
            "label": "List",
            "name": "io.slinkydeveloper.bench.SocialNetworkBenchmark.skipListRouting",
            "color": "rgba(0, 0, 255, 1)"
        },
        {
            "label": "ECTree",
            "name": "io.slinkydeveloper.bench.SocialNetworkBenchmark.ecTreeRouting",
            "color": "rgba(0, 255, 0, 1)"
        },
        {
            "label": "ImmutableECTree",
            "name": "io.slinkydeveloper.bench.SocialNetworkBenchmark.immutableECTreeRouting",
            "color": "rgba(255, 255, 0, 1)"
        }
    ]), "out/social_average.png");

const basicGroups = [
    {
        label: "Tree",
        regexp: /.*route([0-9]{1,2})TreeRouting$/g,
        mainColor: "rgba(255, 0, 0, 1)",
        transparentColor: "rgba(255, 0, 0, 0.3)"
    },
    {
        label: "ECTree",
        regexp: /.*route([0-9]{1,2})ECTreeRouting$/g,
        mainColor: "rgba(0, 255, 0, 1)",
        transparentColor: "rgba(0, 255, 0, 0.3)"
    },
    {
        label: "ImmutableECTree",
        regexp: /.*route([0-9]{1,2})immutableECTreeRouting$/g,
        mainColor: "rgba(255, 255, 0, 1)",
        transparentColor: "rgba(255, 255, 0, 0.3)"
    },
    {
        label: "List",
        regexp: /.*route([0-9]{1,2})SkipListRouting$/g,
        mainColor: "rgba(0, 0, 255, 1)",
        transparentColor: "rgba(0, 0, 255, 0.3)"
    }
];

const withLoadGroups = [
    {
        label: "Tree with load",
        regexp: /.*route([0-9]{1,2})TreeWithLoad$/g,
        mainColor: "rgba(255, 150, 0, 1)",
        transparentColor: "rgba(255, 150, 0, 0.3)",
        scale: 11
    },
    {
        label: "ECTree with load",
        regexp: /.*route([0-9]{1,2})ECTreeWithLoad$/g,
        mainColor: "rgba(150, 255, 0, 1)",
        transparentColor: "rgba(150, 255, 0, 0.3)",
        scale: 11
    },
    {
        label: "ImmutableECTree with load",
        regexp: /.*route([0-9]{1,2})immutableECTreeWithLoad$/g,
        mainColor: "rgba(255, 255, 150, 1)",
        transparentColor: "rgba(255, 255, 150, 0.3)",
        scale: 11
    },
    {
        label: "List with load",
        regexp: /.*route([0-9]{1,2})ListWithLoad$/g,
        mainColor: "rgba(0, 150, 255, 1)",
        transparentColor: "rgba(0, 150, 255, 0.3)",
        scale: 11
    }
];

const allGroups = basicGroups.concat(withLoadGroups);

//doChart(1200, 600, buildChartOptionsForTestsWithGroups(complex_data, complex_tests, basicGroups), "out/basic_complex.png");
//doChart(1200, 600, buildChartOptionsForTestsWithGroups(complex_data, complex_tests, withLoadGroups), "out/with_load_complex.png");
//doChart(1200, 600, buildChartOptionsForTestsWithGroups(complex_data, complex_tests, allGroups), "out/complex_complete.png");

doChart(2000, 600, buildChartOptionsForTestsWithGroups(social_data, social_tests, basicGroups), "out/basic_social.png");
doChart(2000, 600, buildChartOptionsForTestsWithGroups(social_data, social_tests, withLoadGroups), "out/with_load_social.png");
doChart(2000, 600, buildChartOptionsForTestsWithGroups(social_data, social_tests, allGroups), "out/social_complete.png");