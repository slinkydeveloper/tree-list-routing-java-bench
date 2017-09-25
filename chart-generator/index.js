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

function buildChartOptionsForTests(data, regexp1, regexp2, tests, label1, label2) {
    let datasets = [];

    let chartData1 =
        data
            .filter((value) => runRegex(regexp1, value.benchmark) != undefined)
            .map((value) => ({
                index: parseInt(runRegex(regexp1, value.benchmark)),
                score: value.primaryMetric.score,
                rawData: _.flatten(value.primaryMetric.rawData)
            }));
    chartData1.sort((a, b) => a.index - b.index);

    datasets.push({
        label: label1,
        backgroundColor: "#ff0000",
        borderColor: "#ff0000",
        fill: false,
        data: chartData1.map((value) => value.score)
    });

    for (let i = 0; i < chartData1[0].rawData.length; i++) {
        datasets.push({
            label: label1 + "-Iteration" + (i + 1),
            backgroundColor: "rgba(255,0,0,0.3)",
            borderColor: "rgba(255,0,0,0.3)",
            fill: false,
            data: chartData1.map((value) => value.rawData[i])
        });
    }

    let chartData2 =
        data
            .filter((value) => runRegex(regexp2, value.benchmark) != undefined)
            .map((value) => ({
                index: parseInt(runRegex(regexp2, value.benchmark)),
                score: value.primaryMetric.score,
                rawData: _.flatten(value.primaryMetric.rawData)
            }));
    chartData2.sort((a, b) => a.index - b.index);

    datasets.push({
        label: label2,
        backgroundColor: "#0000ff",
        borderColor: "#0000ff",
        fill: false,
        data: chartData2.map((value) => value.score)
    });

    for (let i = 0; i < chartData2[0].rawData.length; i++) {
        datasets.push({
            label: label2 + "-Iteration" + (i + 1),
            backgroundColor: "rgba(0,0,255,0.3)",
            borderColor: "rgba(0,0,255,0.3)",
            fill: false,
            data: chartData2.map((value) => value.rawData[i])
        });
    }

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
                    filter: (l) => l.text == "Tree" || l.text == "List"
                }
            }
        }
    }
}

function buildChartOptionsForAverage(data, name1, name2, label1, label2) {
    return {
        type: 'bar',
        data: {
            labels: [label1, label2],
            datasets: [
                {
                    label: "Values",
                    backgroundColor: ["#ff0000", "#0000ff"],
                    data: [data.find((el) => el.benchmark == name1).primaryMetric.score, data.find((el) => el.benchmark == name2).primaryMetric.score]
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
    }
}

function doChart(length, height, chartOptions, imageName) {
    let chartNode = new ChartjsNode(length, height);
    chartNode.drawChart(chartOptions)
        .then(buffer => {
            return chartNode.getImageStream('image/png');
        }).then(streamResult => {
        return chartNode.writeImageToFile('image/png', imageName);
    });
}

doChart(400, 800, buildChartOptionsForAverage(complex_data, "io.slinkydeveloper.bench.ComplexRegexBenchmark.treeRouting", "io.slinkydeveloper.bench.ComplexRegexBenchmark.skipListRouting", "Tree", "List"), "complex_average.png");
doChart(400, 800, buildChartOptionsForAverage(social_data, "io.slinkydeveloper.bench.SocialNetworkBenchmark.treeRouting", "io.slinkydeveloper.bench.SocialNetworkBenchmark.skipListRouting", "Tree", "List"), "social_average.png");

const basicGroups = [
    {
        label: "Tree",
        regexp: /.*route([0-9]{1,2})Tree$/g,
        mainColor: "rgba(255, 0, 0, 1)",
        transparentColor: "rgba(255, 0, 0, 0.3)"
    },
    {
        label: "List",
        regexp: /.*route([0-9]{1,2})List$/g,
        mainColor: "rgba(0, 0, 255, 1)",
        transparentColor: "rgba(0, 0, 255, 0.3)"
    }
];

const withLoadGroups = [
    {
        label: "Tree with load",
        regexp: /.*route([0-9]{1,2})TreeWithLoad$/g,
        mainColor: "rgba(255, 200, 0, 1)",
        transparentColor: "rgba(255, 200, 0, 0.3)",
        scale: 11
    },
    {
        label: "List with load",
        regexp: /.*route([0-9]{1,2})ListWithLoad$/g,
        mainColor: "rgba(0, 255, 255, 1)",
        transparentColor: "rgba(0, 255, 255, 0.3)",
        scale: 11
    }
];

const allGroups = basicGroups.concat(withLoadGroups);

doChart(1200, 600, buildChartOptionsForTestsWithGroups(complex_data, complex_tests, basicGroups), "out/basic_complex.png");
doChart(1200, 600, buildChartOptionsForTestsWithGroups(complex_data, complex_tests, withLoadGroups), "out/with_load_complex.png");
doChart(1200, 600, buildChartOptionsForTestsWithGroups(complex_data, complex_tests, allGroups), "out/complex_complete.png");

doChart(2000, 600, buildChartOptionsForTestsWithGroups(social_data, social_tests, basicGroups), "out/basic_social.png");
doChart(2000, 600, buildChartOptionsForTestsWithGroups(social_data, social_tests, withLoadGroups), "out/with_load_social.png");
doChart(2000, 600, buildChartOptionsForTestsWithGroups(social_data, social_tests, allGroups), "out/social_complete.png");
