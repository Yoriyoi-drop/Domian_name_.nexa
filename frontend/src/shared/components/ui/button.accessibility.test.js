import { render, screen } from '@testing-library/react';
import { axe, toHaveNoViolations } from 'jest-axe';
import { Button } from '@/shared/components/ui/button';

// Extend Jest with axe matchers
expect.extend(toHaveNoViolations);

// Mock the clsx and tailwind-merge functions
jest.mock('clsx', () => {
  return {
    __esModule: true,
    default: (...classes) => classes.filter(Boolean).join(' '),
  };
});

jest.mock('tailwind-merge', () => {
  return {
    __esModule: true,
    default: (...classes) => classes.filter(Boolean).join(' '),
  };
});

describe('Button Component Accessibility', () => {
  test('button has no accessibility violations', async () => {
    const { container } = render(<Button>Accessible Button</Button>);
    
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });

  test('disabled button has proper accessibility attributes', async () => {
    const { container } = render(<Button disabled>Disabled Button</Button>);
    
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });

  test('loading button has proper accessibility attributes', async () => {
    const { container } = render(<Button loading>Loading Button</Button>);
    
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});