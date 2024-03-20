import unittest
from func import func
from math import sin


class TestFunc(unittest.TestCase):

    def test_1_zero(self):
        x = 0
        eps = 0.1
        expected_value = 0
        value = func(x, eps)
        self.assertEqual(expected_value, value, "Oops")

    def test_2_equal(self):
        x = 0.6
        eps = 0.2
        expected_value = sin(x)
        value = func(x, eps)
        self.assertAlmostEqual(expected_value, value, delta=eps)

    def test_3_equal(self):
        x = -0.000008
        eps = 1e-10
        expected_value = sin(x)
        value = func(x, eps)
        self.assertAlmostEqual(expected_value, value, delta=eps)

    def test_4_equal(self):
        x = 0.01
        eps = 0.0000002
        expected_value = sin(x)
        value = func(x, eps)
        self.assertAlmostEqual(expected_value, value, delta=eps)

    def test_5_true(self):
        x = 0.0001
        eps = 0.0000002
        expected_value = sin(x)
        value = func(x, eps)
        self.assertTrue(expected_value == value)

    def test_6_less(self):
        x1 = 0.0001
        x2 = 0.01
        eps = 0.00001
        value1 = func(x1, eps)
        value2 = func(x2, eps)
        self.assertLess(value1, value2)

